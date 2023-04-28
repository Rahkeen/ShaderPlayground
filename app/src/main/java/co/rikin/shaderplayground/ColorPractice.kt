package co.rikin.shaderplayground

import android.graphics.RuntimeShader

const val Core = """
float3 colorA = vec3(0.83529, 0.77647, 0.87843); // lavender
float3 colorB = vec3(0.09804, 0.16471, 0.31765); // dark blue
float pi = 3.1415926;
"""

const val ExerciseOne = """
  // imports
  $Core
 
  // code
  
  // transition functions
  float linear(float t) {
    return t;
  }
  
  float smooth(float t) {
    return smoothstep(0.0, 1.0, t);
  }
  
  float exponentialIn(float t) {
    return t == 0.0 ? t : pow(2.0, 10.0 * (t - 1.0));
  }
  
  float backIn(float t) {
    return pow(t, 3.0) - t * sin(t * pi);
  }
  
  float circularIn(float t) {
    return 1.0 - sqrt(1.0 - t * t);
  }
  
  float bounceOut(float t) {
    const float a = 4.0 / 11.0;
    const float b = 8.0 / 11.0;
    const float c = 9.0 / 10.0;

    const float ca = 4356.0 / 361.0;
    const float cb = 35442.0 / 1805.0;
    const float cc = 16061.0 / 1805.0;

    float t2 = t * t;

    return t < a
    ? 7.5625 * t2
    : t < b
      ? 9.075 * t2 - 9.9 * t + 3.4
      : t < c
        ? ca * t2 - cb * t + cc
        : 10.8 * t * t - 20.52 * t + 10.72;
  }

  float bounceIn(float t) {
    return 1.0 - bounceOut(1.0 - t);
  }

  float bounceInOut(float t) {
    return t < 0.5
    ? 0.5 * (1.0 - bounceOut(1.0 - t * 2.0))
    : 0.5 * bounceOut(t * 2.0 - 1.0) + 0.5;
  }
  
  half4 color(float2 fragCoord)  {
    float3 color = float3(0.0);
    float t = iTime * 0.5;
    float pct = smooth(abs(fract(t) * 2.0 - 1.0));
    color = mix(colorA, colorB, pct);
    return half4(color, 1.0);
  }
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