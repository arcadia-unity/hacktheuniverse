(ns visual.planet
  (:use arcadia.core)
  (:require [arcadia.hydrate :as hydrate]
            [data.core :as data]
            [universe.state :as state])
  (:import [UnityEngine Debug GameObject Time Resources Vector3]))

(defcomponent Planet [^Vector3 civ
                      ^Vector3 sig])

(defcomponent Spin [^float speed]
  (Start [this]
         ;; random initial tilt
         (set! (.. this transform localEulerAngles)
               (Vector3. -90 0 0))
         (.. this transform
             (Rotate (* 30 (rand)) 0 0)))
  (Update [this]
          (.. this transform
              (Rotate 0 0 (* speed Time/deltaTime)))))

(def material-names ["GasGiant01"
                     "tc-earth_daymap"
                     "wip___gas_giant_texture_by_terranabassador-d358lcd"
                     "GasGiant02"
                     "GasGiant03-1"
                     "pic_new1"
                     "Planet_texture___Cloud_by_Qzma"
                     "planet_texture_by_bbbeto-d3bke08"
                     "planet_Scarl1200"
                     "texture.1"
                     "GasGiant03"])

(defn random-material []
  (-> material-names
      rand-nth
      Resources/Load))

(defn random-planet [^Vector3 position ^String name]
  (let [p (-> (Resources/Load "Planet")
              (instantiate position))]
    (set! (.. p renderer material) (random-material))
    (set! (.. p name) name)))

(defn layout-universe [^double scale]
  (doseq [planet data/planets]
    (random-planet (Vector3/op_Multiply scale
                                        (planet :position))
                   (planet :name))))

;; (layout-universe 10)
(comment
  (doseq [o (objects-typed visual.planet.Spin)]
    (add-component (.gameObject o) visual.planet.Planet))
  
  
  (.. (get-component (Selection/activeObject) visual.planet.Planet) civ)
  
  (.GetComponents (Selection/activeObject) UnityEngine.Component)
  
  (hydrate/populate! (Selection/activeObject)
                     {visual.planet.Planet [{:sig [1.2 4.4 99.8]}]}))

(defn children [^GameObject go]
  (seq (.. go transform)))

(hydrate/register-type visual.planet.Planet)

(def wave-prefab (Resources/Load "Wave"))

(defn map-by [x f]
  (zipmap (map f x) x))

(defn update-universe [state]
  
  (let [statemap (map-by (take 2 (-> state :planets)) :name)]
    (doseq [name (keys statemap)]
      (let [^GameObject obj (object-named name)
            planet (statemap name)]
        
        ;; (hydrate/populate! obj {visual.planet.Planet [planet]})
        (set! (.. (get-component obj visual.planet.Planet) civ) (planet :civ))
        (set! (.. (get-component obj visual.planet.Planet) sig) (planet :signature))
        
        (doseq [c (children obj)]
          (set! (.. c gameObject active) false))
        
        (doseq [sigmap (planet :signals)]
          (let [newsigobj (-> (instantiate wave-prefab
                                           (.. obj transform position)))
                r (sigmap :radius)]
            (set! (.. newsigobj transform localScale) (Vector3. r r 0))
            (set! (.. newsigobj transform parent) (.. obj transform))))))))

(defn crank! []
  (swap! state/state
         #(state/advance-world % (+ (% :time) 0.5)))
   (update-universe @state/state)
  )

(defcomponent Crank []
  (Update [this]
          (crank!)))

(comment 
  (children (object-named "haskids")))