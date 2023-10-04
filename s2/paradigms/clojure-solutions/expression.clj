; ------functional--------
(def constant constantly)
(defn variable [name]
  (fn [vars] (vars name)))

(defn calculate [op]
  (fn [& expressions]
    (fn [vars] (apply op (map #(% vars) expressions)))))

(defn division
  ([] 0)
  ([arg] (if (zero? arg) ##Inf (/ arg)))
  ([arg & args] (if (some zero? args) (identity ##Inf) (apply / (cons arg args)))))

(def add (calculate +))
(def subtract (calculate -))
(def multiply (calculate *))
(def divide (calculate division))
(def negate (calculate -))

(defn calcSumExp [& args] (apply + (map #(Math/exp %) args)))

(def sumexp
  (calculate calcSumExp))

(def lse
  (calculate (fn [& args] (Math/log (apply calcSumExp args)))))

(defn parser [const variable operations]
  (fn [string-expr]
    ((fn parse [token]
       (cond (number? token) (const token)
             (symbol? token) (variable (name token))
             :else (apply (operations (first token)) (map #(parse %) (rest token)))))
     (read-string string-expr))))

(def parseFunction
  (parser constant variable
          {'+ add '- subtract '/ divide '* multiply 'negate negate 'sumexp sumexp 'lse lse}))

; ------------object-------------

(defn evaluate [expr vars] (.evaluate expr vars))
(defn toString [expr] (.toString expr))
(defn diff [expr var] (.diff expr var))

(definterface Expression
  (^Number evaluate [vars])
  (^String toString [])
  (diff [var]))

(declare zero)

(deftype Const [val]
  Expression
  (evaluate [this vars] (.-val this))
  (toString [this] (str (.-val this)))
  (diff [this var] zero))

(defn Constant [val] (Const. val))

(def zero (Constant 0))
(def one (Constant 1))
(def two (Constant 2))

(deftype Var [variable]
  Expression
  (evaluate [this vars] (vars (.-variable this)))
  (toString [this] (str (.-variable this)))
  (diff [this var] (if (= var (.-variable this)) one zero)))

(defn Variable [var] (Var. var))

(declare Add Subtract Multiply Divide Negate)

(defn applymap [op fn expressions]
  (apply op (map fn expressions)))

(defn calc-mean-squares [& args]
  (/ (apply + (map #(* % %) args)) (count args)))

(defn diff-add-sub [class var expressions]
  (applymap class #(diff % var) expressions))

(defn diff-mult [expressions var]
  (let [mult (apply Multiply expressions)]
    (reduce Add
            (map #(Multiply (Divide mult %) (diff % var)) expressions))))

(defn diff-div-two [var expression1 expression2]
  (Divide
    (Divide
      (Subtract
        (Multiply (diff expression1 var) expression2)
        (Multiply expression1 (diff expression2 var)))
      expression2)
    expression2))

(defn diff-div [expressions var]
  (cond
    (= 0 (count expressions)) zero
    (= 1 (count expressions)) (diff-div-two var one (first expressions))
    :else (diff-div-two var (first expressions) (apply Multiply (rest expressions))))
  )

(defn diff-mean-squares [expressions var]
  (Divide
    (Multiply
      two
      (apply Add (map #(Multiply % (diff % var)) expressions)))
    (Constant (count expressions))))

(deftype AbstractExpression [op symbol diff-fn expressions]
  Expression
  (evaluate [this vars] (applymap op #(evaluate % vars) (.-expressions this)))
  (toString [this]
            ;; через join короче и красивее
    (str "(" symbol (if (empty? (.-expressions this)) " ")
         (apply str (map #(str " " (toString %)) (.-expressions this))) ")"))
  (diff [this var] (diff-fn (.-expressions this) var)))

(defn Add [& expressions]
  (AbstractExpression.
    + "+"
    (fn [expressions var] (diff-add-sub Add var expressions))
    expressions))

(defn Subtract [& expressions]
  (AbstractExpression.
    - "-"
    (fn [expressions var] (diff-add-sub Subtract var expressions))
    expressions))

(defn Multiply [& expressions]
  (AbstractExpression. * "*" diff-mult expressions))

(defn Divide [& expressions]
  (AbstractExpression. division "/" diff-div expressions))

(defn Negate [& expressions]
  (AbstractExpression.
    - "negate"
    (fn [expressions var] (diff-add-sub Negate var expressions))
    expressions))

(defn Meansq [& expressions]
  (AbstractExpression.
    calc-mean-squares
    "meansq" diff-mean-squares
    expressions))

(defn RMS [& expressions]
  (AbstractExpression.
    (fn [& args] (Math/sqrt (apply calc-mean-squares args)))
    "rms"
    (fn [expressions var]
      (Divide
        (diff-mean-squares expressions var)
        (Multiply two (apply RMS expressions))))
    expressions))

(def parseObject
  (parser Constant Variable
          {'+ Add '- Subtract '/ Divide '* Multiply 'negate Negate 'meansq Meansq 'rms RMS}))

;; ------functional--------
;(defn constant [value] (constantly value))
;(defn variable [name]
;  (fn [vars] (vars name)))
;
;(defn calculate [op]
;  (fn [& expressions]
;    (fn [vars] (apply op (map #(% vars) expressions)))))
;
;(defn division
;  ([] 0)
;  ([arg] (if (zero? arg) ##Inf (/ arg)))
;  ([arg & args] (if (some zero? args) (identity ##Inf) (apply / (cons arg args)))))
;
;(def add (calculate +))
;(def subtract (calculate -))
;(def multiply (calculate *))
;(def divide (calculate division))
;(def negate (calculate -))
;
;(defn calcSumExp [& args] (apply + (map #(Math/exp %) args)))
;
;(def sumexp
;  (calculate calcSumExp))
;
;(def lse
;  (calculate (fn [& args] (Math/log (apply calcSumExp args)))))
;
;(defn parser [const variable operations]
;  (fn [string-expr]
;    ((fn parse [token]
;       (cond (number? token) (const token)
;             (symbol? token) (variable (name token))
;             :else (apply (operations (first token)) (map #(parse %) (rest token)))))
;     (read-string string-expr))))
;
;(def parseFunction
;  (parser constant variable
;          {'+ add '- subtract '/ divide '* multiply 'negate negate 'sumexp sumexp 'lse lse}))
;
;; ------------object-------------
;
;(defn evaluate [expr vars] (.evaluate expr vars))
;(defn toString [expr] (.toString expr))
;(defn diff [expr var] (.diff expr var))
;(defn toStringInfix [expr] (.toStringInfix expr))
;
;(definterface Expression
;  (^Number evaluate [vars])
;  (^String toString [])
;  (^String toStringInfix [])
;  (diff [var]))
;
;(declare zero)
;
;(deftype Const [val]
;  Expression
;  (evaluate [this vars] (.-val this))
;  (toString [this] (str (.-val this)))
;  (toStringInfix [this] (str (.-val this)))
;  (diff [this var] zero))
;
;(defn Constant [val] (Const. val))
;
;(def zero (Constant 0))
;(def one (Constant 1))
;(def two (Constant 2))
;
;(deftype Var [variable]
;  Expression
;  (evaluate [this vars] (vars (.-variable this)))
;  (toString [this] (str (.-variable this)))
;  (toStringInfix [this] (str (.-variable this)))
;  (diff [this var] (if (= var (.-variable this)) one zero)))
;
;(defn Variable [var] (Var. var))
;
;(declare Add Subtract Multiply Divide Negate)
;
;(defn applymap [op fn expressions]
;  (apply op (map fn expressions)))
;
;(defn calc-mean-squares [& args]
;  (/ (apply + (map #(* % %) args)) (count args)))
;
;(defn diff-add-sub [class var expressions]
;  ;; :NOTE: логика про получение производных должна быть в общем классе
;  (applymap class #(diff % var) expressions))
;
;(defn diff-mult [expressions var]
;  (let [mult (apply Multiply expressions)]
;    (reduce Add
;            (map #(Multiply (Divide mult %) (diff % var)) expressions))))
;
;(defn diff-div-two [var expression1 expression2]
;  (Divide
;    (Divide
;      (Subtract
;        (Multiply (diff expression1 var) expression2)
;        (Multiply expression1 (diff expression2 var)))
;      expression2)
;    expression2))
;
;(defn diff-div [expressions var]
;  (cond
;    (= 0 (count expressions)) zero
;    (= 1 (count expressions)) (diff-div-two var one (first expressions))
;    :else (diff-div-two var (first expressions) (apply Multiply (rest expressions))))
;  )
;
;(defn diff-mean-squares [expressions var]
;  (Divide
;    (Multiply
;      two
;      (apply Add (map #(Multiply % (diff % var)) expressions)))
;    (Constant (count expressions))))
;
;(deftype AbstractExpression [op symbol diff-fn expressions]
;  Expression
;  (evaluate [this vars] (applymap op #(evaluate % vars) (.-expressions this)))
;  (toString [this]
;    (str "(" symbol (if (empty? (.-expressions this)) " ")
;         (apply str (map #(str " " (toString %)) (.-expressions this))) ")"))
;  (toStringInfix [this]
;    (cond
;      (= 1 (count expressions)) (str symbol "(" (toStringInfix (first (.-expressions this))) ")")
;      :else (str "(" (toStringInfix (first (.-expressions this))) " " symbol " " (toStringInfix (last (.-expressions this))) ")")))
;  (diff [this var] (diff-fn (.-expressions this) var)))
;
;(defn Add [& expressions]
;  (AbstractExpression.
;    + "+"
;    (fn [expressions var] (diff-add-sub Add var expressions))
;    expressions))
;
;(defn Subtract [& expressions]
;  (AbstractExpression.
;    - "-"
;    (fn [expressions var] (diff-add-sub Subtract var expressions))
;    expressions))
;
;(defn Multiply [& expressions]
;  (AbstractExpression. * "*" diff-mult expressions))
;
;(defn Divide [& expressions]
;  (AbstractExpression. division "/" diff-div expressions))
;
;(defn Negate [& expressions]
;  (AbstractExpression.
;    - "negate"
;    (fn [expressions var] (diff-add-sub Negate var expressions))
;    expressions))
;
;(defn Meansq [& expressions]
;  (AbstractExpression.
;    calc-mean-squares
;    "meansq" diff-mean-squares
;    expressions))
;
;(defn RMS [& expressions]
;  (AbstractExpression.
;    (fn [& args] (Math/sqrt (apply calc-mean-squares args)))
;    "rms"
;    (fn [expressions var]
;      (Divide
;        (diff-mean-squares expressions var)
;        (Multiply two (apply RMS expressions))))
;    expressions))
;
;(def parseObject
;  (parser Constant Variable
;          {'+ Add '- Subtract '/ Divide '* Multiply 'negate Negate 'meansq Meansq 'rms RMS}))
;
;
;
;;--------------------infix-----------------
;
;(load-file "parser.clj")
;
;(def *digit (+char "0123456789"))
;(def *number-natural (+str (+plus *digit)))
;(def *number (+map read-string (+or (+seqf str *number-natural (+char ".") *number-natural) *number-natural)))
;(def *space (+char " \t\n\r"))
;(def *ws (+ignore (+star *space)))
;(def *binary-operator (+char "+-/*"))
;(def *unary-operator (+seqf str (+char "n") (+char "e") (+char "g") (+char "a") (+char "t") (+char "e")))
;(def *variable (+char "xyz"))
;(def *open-bracket (+ignore (+char "(")))
;(def *close-bracket (+ignore (+char ")")))
;
;(defn *expression []
;  (let [*value
;        (+or
;          *number
;          *variable
;          (delay (*expression)))]
;    (+or
;      (+or
;        (+seq *unary-operator *ws *value)
;        (+seq *value *ws *binary-operator *ws *value)
;        *value)
;      )
;    (+or *open-bracket)))
;
;
;
;
;;(let [*value (+or
;;               *number
;;               *variable
;;               (+seqn 0 *unary-operator *ws (delay (*expression)))
;;               (+seqn 0 (delay (*expression)) *ws *binary-operator *ws (*expression))
;;
;;               )]
;;  (+or
;;    *value
;;    (+seq *open-bracket *ws *value *ws *close-bracket)
;;    )))
;
;
;
;;(letfn [(*value [] (+seqn 0 *ws (+or
;;               *number
;;               *variable
;;               (+seq *unary-operator *ws (delay (*expression)))
;;               (+seq  (delay (*expression)) *ws *binary-operator *ws  (*expression))) *ws))]
;;  (+or
;;    (+seqn 0 *open-bracket  (*value) *close-bracket)
;;     (*value))))
;
;
;
;(def expr-parser (+parser (+seqn 0 *ws (delay (*expression)) *ws)))
;
;(def operations {\+ Add \- Subtract \/ Divide '\* Multiply "negate" Negate})
;
;(defn parseObjectInfix [string-expr]
;  ((fn parse [token]
;     (cond (number? token) (Constant token)
;           (char? token) (Variable (str token))
;           (= 2 (count token)) (apply (operations (first token)) [(parse (last token))])
;           :else (apply (operations (second token)) [(parse (first token)) (parse (last token))])))
;   (expr-parser string-expr)))
;
;(def expr "negate x / 2.0")
;(println (expr-parser expr))
;(def res (parseObjectInfix expr))
;(println res)
