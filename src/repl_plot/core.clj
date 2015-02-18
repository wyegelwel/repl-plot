(ns repl-plot.core)

(defn- data-range
  "Returns the min and max of the list as [min, max]"
  [xs]
  [(apply min xs) (apply max xs)])

(defn- range-step
  "Returns the step size between two spaces in the grid along an axis."
  [x-min x-max max-width]
  (/ (- x-max x-min) max-width)) 

(defn- draw-x-axis
  "Draws the x axis with the numbers running vertically"
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
  "Find the closest number to n in the interval"
  [n interval]
  (first (apply min-key second (map (fn [k] [k (Math/abs (- k n))]) interval))))

(defn- x-y-to-matrix
  "Maps the xy pairs into an adjency list represented as a map, where the keys
   are [x,y] pairs and the values are what should be shown in that space. If a
   space is nil, nothing should be in that space."
  [xy-pairs]
  (into {} (map (fn [pair] [pair "*"]) xy-pairs))) 

(defn- draw-matrix
  "Given an adjency list, draw the spaces in the terminal."
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
  "Take intervals of doubles and turn them into strings we can display."
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

(defn- intervalize
  [xs max-intervals]
  (let [[x-min x-max] (data-range xs)
        x-step (range-step x-min x-max max-intervals)]
    (range x-min (+ x-max x-step) x-step)))

(defn plot
  "Plot a scatter plot in the terminal. xs and ys are expected to be lists of
   numbers where i-th number in xs is paired with the i-th number in ys.
  
   The display of the plot can be tuned by providing key-value pairs.
       :max-width is the number of columns to use in the terminal. 
       :max-height is the number of rows to use in the terminal.
       :x-axis-display-step the number of spaces between x ticks on the axis
       :precision for the x and y axis, the number of decimal points to display"
  [xs ys & {:keys [max-width max-height x-axis-display-step precision]
            :or {max-width 60 max-height 30 x-axis-display-step 15 precision 1}}]
  (let [x-intervals (intervalize xs max-width)
        y-intervals (intervalize ys max-height)
        xs-intervalized (map #(find-closest % x-intervals) xs)
        ys-intervalized (map #(find-closest % y-intervals) ys)
        xy-pairs (partition 2 (interleave xs-intervalized ys-intervalized)) 
        x-display (displayify-intervals x-intervals precision false)
        y-display (displayify-intervals y-intervals precision true)
        y->y-display (zipmap y-intervals y-display)
        xy-matrix (x-y-to-matrix xy-pairs)]
    (draw-matrix x-intervals y-intervals xy-matrix #(print (y->y-display %) "|"  ) identity)
    (draw-x-axis x-display x-axis-display-step max-width (fn [] (apply str (repeat (inc (apply max (map count y-display))) " "))) (fn [] ""))))

(defn hist
  "Plots a histogram into the terminal. data is expected to be a list of tuples,
   where the first is the category and the second is the count
  
   The display of the histogram can be tuned by providing key-value pairs.
       :max-items is the number of items we will display before scaling."
  [data & {:keys [max-items] :or {max-items 60}}]
  (let [max-count (apply max (map second data))
        max-items (min max-count max-items)
        max-category-len (apply max (map (comp count str first) data))
        max-count-len (apply max (map (comp count str second) data))]
    (doseq [[category cnt] data]
      (printf (str "%" max-category-len "s %" max-count-len "d %s\n")
              category
              cnt
              (clojure.string/join "" (repeat (* max-items (/ cnt max-count)) "#"))))))

(comment (plot (repeatedly 100 #(rand 10)) (repeatedly 100 #(rand 10)) :max-width 60 :max-height 20 :display-step 15.0 :precision 1)
         (plot (range 0 1.05 0.1) (map #(* % %) (range 0 1.05 0.1)) :max-width 60.0 :max-height 30.0 :display-step 15 :precision 1)
         (hist [[:apples (rand-int 300)] [:bananas (rand-int 200)] [:oranges (rand-int 150)]]))
