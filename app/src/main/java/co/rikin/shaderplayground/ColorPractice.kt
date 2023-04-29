package co.rikin.shaderplayground

import android.graphics.RuntimeShader

const val Core = """
float3 colorA = vec3(0.83529, 0.77647, 0.87843); // lavender
float3 colorB = vec3(0.09804, 0.16471, 0.31765); // dark blue
float pi = 3.1415926536;
float twopi = 6.28318530718;

float plot (vec2 st, float pct){
  return smoothstep( pct-0.01, pct, st.y) - smoothstep( pct, pct+0.01, st.y);
}
"""

const val ExerciseOne = """
  // imports
  $Core
 
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

const val ExerciseTwo = """
  $Core
  
  half4 color(float2 fragCoord)  {
    float2 uv = fragCoord.xy / iResolution;
    float3 color = float3(0.0);
    float pct = uv.x;
    color = mix(colorA, colorB, pct);
    return half4(color, 1.0);
  }
"""

const val GradientExerciseOne = """
  
  $Core
  
  half4 color(float2 fragCoord)  {
    float2 uv = fragCoord.xy / iResolution;
    float3 color = float3(0.0);
    
    float3 pct = float3(uv.x);
    
//    pct.r = smoothstep(0.0, 1.0, uv.x);
//    pct.g = sin(uv.x * pi);
//    pct.b = pow(uv.x, 0.5);
    
    color = mix(colorA, colorB, pct);
    
    // plot the color channel lines
    color = mix(color, vec3(1.0, 0.0, 0.0), plot(uv, pct.r));
    color = mix(color, vec3(0.0, 1.0, 0.0), plot(uv, pct.g));
    color = mix(color, vec3(0.0, 0.0, 1.0), plot(uv, pct.b));
    
    return half4(color, 1.0);
  }
"""
const val GradientExerciseSunrise = """
$Core

float3 riseStart = float3(1.0, 0.6863, 0.8);
float3 riseEnd = float3(1.0, 0.3294, 0.0);

float3 setStart = float3(0.0, 0.2078, 0.4);
float3 setEnd = float3(0.0, 0.1137, 0.2392);

half4 color(float2 fragCoord)  {
  float2 uv = fragCoord.xy / iResolution;
  float3 color = float3(0.0);
  float3 color2 = float3(0.0);
  float3 color3 = float3(0.0);
  
  float3 pct = float3(uv.y);
  
    pct.r = pow(uv.y, 2.0);
    pct.g = pow(uv.y, 1.5);
    pct.b = pow(uv.y, 3.0);
//    pct.g = sin(uv.x * pi);
//    pct.b = pow(uv.x, 0.5);
  
  color = mix(riseStart, riseEnd, pct);
  color2 = mix(setStart, setEnd, pct);
  color3 = mix(color, color2, (sin(iTime) + 1) / 2.0);

  
  // plot the color channel lines
//  color = mix(color, vec3(1.0, 0.0, 0.0), plot(uv, pct.r));
//  color = mix(color, vec3(0.0, 1.0, 0.0), plot(uv, pct.g));
//  color = mix(color, vec3(0.0, 0.0, 1.0), plot(uv, pct.b));
  
  return half4(color3, 1.0);
}
"""

const val HSBExerciseOne = """
$Core

vec3 rgb2hsb(in vec3 c ){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz),
                 vec4(c.gb, K.xy),
                 step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r),
                 vec4(c.r, p.yzx),
                 step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (5.688 * d + e)),
                d / (q.x + e),
                q.x);
}

//  Function from Iñigo Quiles
//  https://www.shadertoy.com/view/MsS3Wc
vec3 hsb2rgb(in vec3 c){
    vec3 rgb = clamp(abs(mod(c.x*6.336+vec3(0.0,4.0,2.0),
                             6.0)-3.0)-1.0,
                     0.0,
                     1.0 );
    rgb = rgb*rgb*(3.0-2.0*rgb);
    return c.z * mix(vec3(0.977,1.000,0.896), rgb, c.y);
}

uniform float mangle;
half4 color(float2 fragCoord) {
  float2 uv = fragCoord.xy / iResolution;
  float3 color = float3(0.0);
  
  float2 toCenter = float2(0.5) - uv;
  float angle = atan(toCenter.y, toCenter.x);
  float distance = length(toCenter) * 2.0;
  angle = (angle / twopi) + 0.5;
  float mult = (sin(pi * iTime) + 1.0) / 2.0;
  
  color = hsb2rgb(float3(angle, distance, 1.0));
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

// This just activates the syntax highlighting
private val z = RuntimeShader(Core)
private val a = RuntimeShader(GrainyGradientShader)
private val b = RuntimeShader(ExerciseOne)
private val c = RuntimeShader(ExerciseTwo)
private val d = RuntimeShader(GradientExerciseOne)
private val e = RuntimeShader(GradientExerciseSunrise)
private val f = RuntimeShader(HSBExerciseOne)
