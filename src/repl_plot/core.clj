(ns repl-plot.core)

(defn s [x]
  (println x)
  x)

(defn- draw-x-axis
  [x-display display-step max-width precision row-pre-print row-post-print]
  (let [intervals-to-display (map first (partition-all (int display-step) x-display))]
    (println (row-pre-print) (apply str (repeat (inc max-width) "-")))
    (doseq [row-nums (partition (count intervals-to-display) 
                                (apply interleave intervals-to-display))]
      (print (row-pre-print) "")
      (doseq [n row-nums]
        (print (apply str n (repeat (dec display-step) " "))))
      (print (row-post-print)"\n"))))

(defn- find-closest 
  [n intervals]
  (first (apply min-key second (map (fn [k] [k (Math/abs (- k n))]) intervals))))

(defn- x-y-to-matrix 
  [xs ys x-intervals y-intervals]
  (let [xs-intervalized (map #(find-closest % x-intervals) xs)
        ys-intervalized (map #(find-closest % y-intervals) ys)
        xy-pairs (partition 2 (interleave xs-intervalized ys-intervalized))]
      (into #{} xy-pairs)))

(defn- draw-matrix 
  [x-intervals y-intervals xy-set row-pre-print row-post-print]
  (doseq [y (reverse y-intervals)]
    (row-pre-print y)
    (doseq [x x-intervals]
      (if (xy-set [x y])
        (print "*")
        (print " ")))
    (row-post-print y)
    (print "\n")))

(defn- data-range 
  [xs]
  [(apply min xs) (apply max xs)])

(defn- range-step 
  [x-min x-max max-width]
  (/ (- x-max x-min) max-width))

(defn- displayify-intervals 
  [intervals precision prepend-padding?]
  (let [format-precision #(format (str "%." (int precision) "f") (double %))
        str-intervals (map format-precision intervals)
        str-max (apply max (map count str-intervals))
        prepend-true [#(apply str (repeat (- str-max (count %)) " ")) (fn [_] "")] 
        [prepend-fn append-fn] (if prepend-padding? 
                                 prepend-true
                                 (reverse prepend-true))]
    (map (fn [x]
           (str (prepend-fn x) x (append-fn x)))
         str-intervals)))

(defn plot 
  [xs ys & {:keys [max-width max-height display-step precision]}]
  (let [[x-min x-max] (data-range xs)
        [y-min y-max] (data-range ys)
        x-step (range-step x-min x-max max-width)
        y-step (range-step y-min y-max max-height)
        x-intervals (range x-min (+ x-max x-step) x-step)
        y-intervals (range y-min (+ y-max y-step) y-step)
        x-display (displayify-intervals x-intervals precision false)
        y-display (displayify-intervals y-intervals precision true)
        y->y-display (zipmap y-intervals y-display)
        xy-intervalized-set (x-y-to-matrix xs ys x-intervals y-intervals)]
    (draw-matrix x-intervals y-intervals xy-intervalized-set #(print (y->y-display %) "|"  ) identity)
    (draw-x-axis x-display display-step max-width precision (fn [] (apply str (repeat (inc (apply max (map count y-display))) " "))) (fn [] ""))))

(plot (repeatedly 10 #(rand 10)) (repeatedly 10 #(rand 10)) :max-width 60.0 :max-height 20 :display-step 15.0 :precision 1)
