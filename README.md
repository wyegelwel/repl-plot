# repl-plot

A library that allows you to plot in the terminal. This is not meant to take this place of nice stylized graphs in your favorite plotting library. repl-plot is here for quick and dirty views of your data, in the repl, so you can refine your ideas.

## Set up

Just add this line to your project.clj:

[![Clojars Project](http://clojars.org/repl-plot/latest-version.svg)](http://clojars.org/repl-plot)

## Plotting

### Scatterplot
For a basic scatterplot, simply supply repl-plot.core/plot with a list of xs and ys. 
```clojure
user=> (use 'repl-plot.core)
nil
user=> (let [xs (range 0 10 0.3)
  #_=>       ys (map #(Math/sin %) xs)]
  #_=>   (plot xs ys))
 1.1 |                                                              
 1.0 |         * *                                   * *            
 0.9 |       *                                     *                
 0.9 |             *                                     *          
 0.8 |     *                                      *                 
 0.7 |                                                              
 0.7 |               *                                     *        
 0.6 |                                          *                   
 0.5 |    *                                                         
 0.5 |                                                              
 0.4 |                *                                      *      
 0.3 |                                        *                     
 0.3 |  *                                                           
 0.2 |                                                              
 0.1 |                  *                                     *     
 0.1 |                                                              
 0.0 |*                                     *                       
-0.1 |                                                              
-0.1 |                    *                                         
-0.2 |                                                          *   
-0.3 |                                    *                         
-0.3 |                                                              
-0.4 |                                                              
-0.5 |                      *                                     * 
-0.5 |                                   *                          
-0.6 |                                                              
-0.7 |                        *                                     
-0.7 |                                                              
-0.8 |                                 *                            
-0.9 |                         *                                    
-0.9 |                               *                              
-1.0 |                           * *                                
      -------------------------------------------------------------
      0              2              5              7              9               
      .              .              .              .              .               
      0              5              0              4              9 
```

There are additional tuning parameters that adjust the look of your plot, here is an example using all of them.
```clojure
user=> (let [xs (range 0 10 0.3)
  #_=>       ys (map #(Math/sin %) xs)]
  #_=>   (plot xs ys :max-width 75 :max-height 35 :x-axis-display-step 10 :precision 2))
 1.00 |           *  *                                            *                 
 0.94 |         *                                               *   *               
 0.88 |                *                                                            
 0.83 |                                                                *            
 0.77 |       *                                               *                     
 0.71 |                                                                             
 0.66 |                  *                                               *          
 0.60 |                                                    *                        
 0.54 |     *                                                                       
 0.49 |                                                                             
 0.43 |                    *                                               *        
 0.37 |                                                                             
 0.31 |  *                                               *                          
 0.26 |                                                                             
 0.20 |                                                                             
 0.14 |                       *                                              *      
 0.09 |                                                                             
 0.03 |                                                *                            
-0.03 |*                                                                            
-0.08 |                                                                             
-0.14 |                         *                                                   
-0.20 |                                                                         *   
-0.26 |                                             *                               
-0.31 |                                                                             
-0.37 |                                                                             
-0.43 |                           *                                                 
-0.48 |                                                                           * 
-0.54 |                                           *                                 
-0.60 |                                                                             
-0.65 |                                                                             
-0.71 |                              *                                              
-0.77 |                                         *                                   
-0.83 |                                                                             
-0.88 |                                *                                            
-0.94 |                                       *                                     
-1.00 |                                  * *                                        
       ----------------------------------------------------------------------------
       0         1         2         3         5         6         7         9          
       .         .         .         .         .         .         .         .          
       0         3         6         9         2         6         9         2          
       0         2         4         6         8         0         2         4           
```
Take a look at the doc string for repl-plot.core/plot for an explanation of each.

Finally, it is worth mentioning that the plot can start to look wonky when you data is too dense with respect to the size of the grid (max-with x max-height). The reason for this is that continous points must be mapped to intervals. If the intervals are large, you may lose resolution of the data.

### Histogram

A basic example of the histogram functionality:

```clojure
user=> (hist [[:apples 5] [:bananas 2] [:oranges 1]])
 :apples 5 #####
:bananas 2 ##
:oranges 1 #
```

The histogram function will not bucket or sort for you, you are expected to do both. This is by design to give you the full flexibility in using the function.




Copyright © 2015 

Distributed under the Eclipse Public License, the same as Clojure.
