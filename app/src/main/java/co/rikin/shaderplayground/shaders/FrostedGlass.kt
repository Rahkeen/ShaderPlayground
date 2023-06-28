package co.rikin.shaderplayground.shaders

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.rikin.shaderplayground.ui.theme.ShaderPlaygroundTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
val kirilGlassShader = """
  uniform shader composable;
  uniform float2 rectangle;
  uniform float radius;
  
  float random (float2 st) {
      return fract(sin(dot(st.xy,vec2(12.9898,78.233)))*43758.5453123);
  }
  
  
  float roundRect(float2 position, float2 box, float radius) {
      vec2 q = abs(position) - box + radius;
      return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius;   
  }
  
  half4 avgColor(float2 coord) {
      half4 color = half4(0.0);
      for(int x = -1; x < 2; x++) {
          for(int y = -1; y < 2; y++) {
              float2 offset = float2(x, y);
              color += composable.eval(coord );
          }
      }
      
      return color / 9.0;
  }
  
  half4 main(float2 coord) {
      float2 rectCenter = float2(rectangle.x, rectangle.y) / 2.0;
      float2 adjustedCoord = coord - rectCenter;
      float distanceFromEdge = roundRect(adjustedCoord, rectCenter, radius);
     
      half4 color = composable.eval(coord);
      if (distanceFromEdge > 0.0) {
          return color;
      }
      
      float2 normCoord = coord / rectangle.xy;
      float rand = random(normCoord);
      half4 black = half4(0.3, 0.3, 0.3, 1.0);
      float4 texture = mix(black, float4(float3(rand), 1.0), 0.4);
      color = mix(color, texture, 0.5);
        
//      color = avgColor(coord);

      
      return color;
  }
""".trimIndent()

@Preview
@Composable
fun FrostedGlassPlayground() {
  ShaderPlaygroundTheme {
    val shader = remember { RuntimeShader(kirilGlassShader) }
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .drawBehind {
          drawCircle(
            brush = Brush.linearGradient(
              colors = listOf(Color(0xFF7A26D9), Color(0xFFE444E1)),
            ),
            center = Offset(
              size.center.x - size.width / 3,
              size.center.y - size.height / 8
            ),
            radius = size.width / 3
          )
        },
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .graphicsLayer {
            with(shader) {
              setFloatUniform(
                "rectangle",
                width,
                height
              )
              setFloatUniform(
                "radius",
                20.dp.toPx()
              )
            }

            val blur = RenderEffect.createBlurEffect(
              10f,
              10f,
              Shader.TileMode.DECAL
            )

            renderEffect = RenderEffect
              .createRuntimeShaderEffect(
                shader,
                "composable"
              )
              .asComposeRenderEffect()
          }
          .width(300.dp)
          .height(200.dp)
          .clip(RoundedCornerShape(20.dp))
          .onSizeChanged {
            width = it.width.toFloat()
            height = it.height.toFloat()
          }
      )
    }
  }
}

