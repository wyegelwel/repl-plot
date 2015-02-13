(ns repl-plot.core)

(defn s [x]
  (println x)
  x)

(defn- data-range
  "Returns the min and max of the list as [min, max]"
  [xs]
  [(apply min xs) (apply max xs)])

(defn- range-step
  "Returns the step size between two spaces in the grid along an axis."
  [x-min x-max max-width]
  (/ (- x-max x-min) max-width)) 

(defn- draw-x-axis
  "Draws the x axis given "
  [x-display display-step max-width row-pre-print row-post-print]
  (let [intervals-to-display (map first (partition-all (int display-step) x-display))]
    (println (row-pre-print) (apply str (repeat (inc max-width) "-")))
    (doseq [row-nums (partition (count intervals-to-display) 
                                (apply interleave intervals-to-display))]
      (print (row-pre-print) "") ; Need to add the "" for an extra space
      (doseq [n row-nums]
        (print (apply str n (repeat (dec display-step) " "))))
      (print (row-post-print)"\n"))))

(defn- find-closest 
  [n intervals]
  (first (apply min-key second (map (fn [k] [k (Math/abs (- k n))]) intervals))))

(defn- x-y-to-matrix 
  [xy-pairs]
  (into {} (map (fn [pair] [pair "*"]) xy-pairs))) 

(defn- draw-matrix 
  [x-intervals y-intervals xy-set row-pre-print row-post-print]
  (doseq [y (reverse y-intervals)]
    (row-pre-print y)
    (doseq [x x-intervals]
      (if (xy-set [x y])
        (print (xy-set [x y]))
        (print " ")))
    (row-post-print y)
    (print "\n")))



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
        xs-intervalized (map #(find-closest % x-intervals) xs)
        ys-intervalized (map #(find-closest % y-intervals) ys)
        xy-pairs (partition 2 (interleave xs-intervalized ys-intervalized)) 
        x-display (displayify-intervals x-intervals precision false)
        y-display (displayify-intervals y-intervals precision true)
        y->y-display (zipmap y-intervals y-display)
        xy-matrix (x-y-to-matrix xy-pairs)]
    (draw-matrix x-intervals y-intervals xy-matrix #(print (y->y-display %) "|"  ) identity)
    (draw-x-axis x-display display-step max-width (fn [] (apply str (repeat (inc (apply max (map count y-display))) " "))) (fn [] ""))))

(plot (repeatedly 100 #(rand 10)) (repeatedly 100 #(rand 10)) :max-width 60 :max-height 20 :display-step 15.0 :precision 1)
(plot (range 0 1.05 0.1) (map #(* % %) (range 0 1.05 0.1)) :max-width 60.0 :max-height 30.0 :display-step 15 :precision 1)
