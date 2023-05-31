package co.rikin.shaderplayground.book

import android.graphics.RuntimeShader
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import co.rikin.shaderplayground.SimpleSketchWithCache
import co.rikin.shaderplayground.ui.theme.ShaderPlaygroundTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
val shapesShader = """
  uniform float2 resolution;
  uniform float time;
  
  half4 main(float2 fragCoord) {
      float2 uv = fragCoord.xy / resolution;
      uv.y = 1.0 - uv.y;
      
      half3 color = half3(0.0);
      
      // bottom left
      float2 b1 = step(float2(0.1), uv);
      float pct = b1.x * b1.y; // this is like logical AND
      
      // top right
      float2 b2 = step(float2(0.1), 1.0 - uv);
      pct *= b2.x * b2.y;
      
      color = half3(pct);
      
      return half4(color, 1.0);
  }
""".trimIndent()


@Preview
@Composable
fun ShapesPlayground() {
  ShaderPlaygroundTheme {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background),
      contentAlignment = Alignment.Center
    ) {
      val shader = remember { RuntimeShader(shapesShader) }
      
      SimpleSketchWithCache(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
      ) {time ->
        onDrawBehind {
          with(shader) {
            setFloatUniform(
              "resolution",
              size.width,
              size.height
            )

            setFloatUniform(
              "time",
              time.value
            )
          }
          drawRect(brush = ShaderBrush(shader))
        }
      }
    }
  }
}