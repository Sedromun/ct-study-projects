(defn vcoords [op vectors]
  {:pre  [(every? #(vector? %) vectors)
          (every? #(every? number? %) vectors)
          (let [len (count (first vectors))]
            (every? #(= len (count %)) vectors))]
   :post [(vector? %)
          (= (count (first vectors)) (count %))]}
  (vec (apply map op vectors)))

(defn v+ [& vectors] (vcoords + vectors))
(defn v- [& vectors] (vcoords - vectors))
(defn v* [& vectors] (vcoords * vectors))
(defn vd [& vectors] (vcoords / vectors))

(defn scalar [& vectors]
  {:pre  [(every? #(vector? %) vectors)
          (every? #(every? number? %) vectors)
          (let [len (count (first vectors))]
            (every? #(= len (count %)) vectors))]
   :post [(number? %)]}
  (reduce + (apply mapv * vectors)))
(defn vect2 [v1 v2]
  [(- (* (nth v1 1) (nth v2 2)) (* (nth v1 2) (nth v2 1)))
   (- (* (nth v1 2) (nth v2 0)) (* (nth v1 0) (nth v2 2)))
   (- (* (nth v1 0) (nth v2 1)) (* (nth v1 1) (nth v2 0)))])
(defn vect [& vectors]
  {:pre  [(every? #(vector? %) vectors)
          (every? #(every? number? %) vectors)
          (every? #(= 3 (count %)) vectors)]
   :post [(vector? %)
          (= (count (first vectors)) (count %))]}
  (reduce vect2 vectors))
(defn v*s [vector & scalars]
  {:pre  [(vector? vector)
          (every? number? scalars)]
   :post [(vector? %)
          (= (count vector) (count %))]}
  (reduce (fn [v s] (mapv #(* s %) v)) vector scalars))
(defn mcoord [op matrices]
  {:pre  [(every? #(vector? %) matrices)
          (apply = (map #(count %) matrices))]
   :post [(vector? %)
          (= (count (first matrices)) (count %))]}
  (vec (apply map op matrices)))
(defn m+ [& matrices] (mcoord v+ matrices))
(defn m- [& matrices] (mcoord v- matrices))
(defn m* [& matrices] (mcoord v* matrices))
(defn md [& matrices] (mcoord vd matrices))
(defn m*s [matrix & scalars] (reduce #(mapv v*s %1 (repeat %2)) matrix scalars))
(defn m*v [matrix vector] (mapv #(scalar vector %) matrix))
(defn transpose [matrix] (apply mapv vector matrix))
(defn m*m [& matrices]
  {:pre  [(every? vector? matrices)
          (every? vector? (map first matrices))]
   :post [(= (count %) (count (first matrices))) (= (count (first %)) (count (first (last matrices))))]}
  (reduce
    (fn [matrix1 matrix2] (transpose (mapv #(m*v matrix1 %) (transpose matrix2))))
    matrices))

(defn tcoord [op tensors]
  {:pre  [(or (every? number? tensors) (let [len (count (first tensors))]
                                         (every? #(= len (count %)) tensors)))]
   :post [(if (number? (first tensors)) (number? %) (vector? %))]}
  (cond (number? (first tensors)) (apply op tensors)
        (number? (first (first tensors))) (vcoords op tensors)
        :else (vec (apply mapv (fn [& args] (tcoord op args)) tensors))))

(defn t+ [& tensors] (tcoord + tensors))
(defn t- [& tensors] (tcoord - tensors))
(defn t* [& tensors] (tcoord * tensors))
(defn td [& tensors] (tcoord / tensors))
