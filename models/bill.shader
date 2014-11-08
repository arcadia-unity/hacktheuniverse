Shader "GLSL shader for billboards" {
   Properties {
      _Color ("Main Color", Color) = (1,1,1,0.5)
   }
   SubShader {
      Tags {"Queue" = "Transparent" }
      Pass {   
         Cull Off
         Blend SrcAlpha OneMinusSrcAlpha
         GLSLPROGRAM
         varying vec4 textureCoordinates; 
  
         #ifdef VERTEX
 
         void main()
         {
            // gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
            gl_Position = gl_ProjectionMatrix 
               * (gl_ModelViewMatrix * vec4(0.0, 0.0, 0.0, 1.0) 
               + vec4(gl_Vertex.x, gl_Vertex.y, 0.0, 0.0));
               
            textureCoordinates = gl_MultiTexCoord0;
         }
 
         #endif
 
         #ifdef FRAGMENT
 
         uniform vec4 _Color;        
         void main()
         {
            float d = distance(textureCoordinates.xy, vec2(0.5, 0.5));
            float a;
            if(d > 0.5) {
               a = 0.;
            } else if(d > 0.45) {
               a = 0.9;
            } else if(d > 0.40) {
               a = 0.8;
            } else if(d > 0.35) {
               a = 0.7;
            } else if(d > 0.30) {
               a = 0.6;
            } else if(d > 0.25) {
               a = 0.5;
            } else {
               a = 0.;
            }
            a = pow(a, 2.5);
            
            gl_FragColor = vec4(_Color.rgb, a);
         }
 
         #endif
 
         ENDGLSL
      }
   }
}
