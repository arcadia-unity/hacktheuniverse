(ns visual.planet
  (:use arcadia.core)
  (:require [data.core :as data])
  (:import [UnityEngine Time Resources Vector3]))

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

(defn random-planet [^Vector3 position]
  (-> (Resources/Load "Planet")
      (instantiate position)
      (#(set! (.. % renderer material) (random-material)))))