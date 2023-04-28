package co.rikin.shaderplayground

import android.graphics.RuntimeShader

const val Core = """
  
"""

const val ExerciseOne = """
  
"""

const val GrainyGradientShader = """
float3 colorA = vec3(0.83529, 0.77647, 0.87843); // lavender
float3 colorB = vec3(0.09804, 0.16471, 0.31765); // dark blue
float pi = 3.1415926;

float random(float2 st) {
    return fract(sin(dot(st.xy, float2(1.9898,78.233))) * 43758.5453123);
}

half4 color(float2 fragCoord) {
    float2 uv = fragCoord.xy / iResolution;
    float3 color = float3(0.0);
    float noise = random(uv);
    float noiseX = uv.x + noise * chaos;
    
    float3 pct = float3(pow(noiseX, 2.0));
    color = mix(colorA, colorB, pct);
    
    return half4(color, 1.0);
}
"""

private val a = RuntimeShader(GrainyGradientShader)
private val b = RuntimeShader(ExerciseOne)