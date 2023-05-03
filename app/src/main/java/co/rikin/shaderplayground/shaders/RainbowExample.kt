package co.rikin.shaderplayground.shaders

import android.graphics.RuntimeShader

const val RainbowShader = """
  $Core
  
  float3 red = float3(1.0, .349, .369);
  float3 orange = float3(1.0, .792, .227);
  float3 green = float3(0.541, 0.788, .149);
  float3 blue = float3(0.098, 0.510, 0.769);
  float3 purple = float3(0.416, 0.298, 0.576);
  
  half4 color(float2 fragCoord) {
    float2 uv = fragCoord.xy / iResolution;
    float3 color = colorB;
    
    float redArc = 1 - sin(pi * uv.x);
    float redOn = plot(uv, redArc);
    float orangeArc = 1.05 - sin(pi * uv.x);
    float orangeOn = plot(uv, orangeArc);
    float greenArc = 1.10 - sin(pi * uv.x);
    float greenOn = plot(uv, greenArc);
    float blueArc = 1.15 - sin(pi * uv.x);
    float blueOn = plot(uv, blueArc);
    float purpleArc = 1.20 - sin(pi * uv.x);
    float purpleOn = plot(uv, purpleArc);
    
    color = mix(color, red, redOn);
    color = mix(color, orange, orangeOn);
    color = mix(color, green, greenOn);
    color = mix(color, blue, blueOn);
    color = mix(color, purple, purpleOn);
    
    return half4(color, 1.0);
  }
"""

private val noop = RuntimeShader(RainbowShader)