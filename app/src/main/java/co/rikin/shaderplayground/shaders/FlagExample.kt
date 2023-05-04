package co.rikin.shaderplayground.shaders

import android.graphics.RuntimeShader

const val FlagShader = """
  $Core
  
  
  half4 color(float2 fragCoord) {
    float2 uv = fragCoord.xy / iResolution;
    float3 color = colorB;
    float stripeWidth = 1.0 / 5.0;
    color = mix(color, red, step(uv.x, stripeWidth));
    color = mix(color, orange, step(uv.x, stripeWidth * 2.0) - step(uv.x, stripeWidth));
    color = mix(color, green, step(uv.x, stripeWidth * 3.0) - step(uv.x, stripeWidth * 2.0));
    color = mix(color, blue, step(uv.x, stripeWidth * 4.0) - step(uv.x, stripeWidth * 3.0));
    color = mix(color, purple, step(uv.x, stripeWidth * 5.0) - step(uv.x, stripeWidth * 4.0));
    
    
    return half4(color, 1.0);
  }
"""

private val noop = RuntimeShader(FlagShader)
