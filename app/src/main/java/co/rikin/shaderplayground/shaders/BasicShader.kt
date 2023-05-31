package co.rikin.shaderplayground.shaders

import android.graphics.RuntimeShader
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import co.rikin.shaderplayground.SimpleSketchWithCache
import co.rikin.shaderplayground.ui.theme.ShaderPlaygroundTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
val shader = """
  uniform float2 resolution;
  
  half4 main(float2 coord) {
      float2 uv = coord.xy / resolution;
          
      return half4(uv.x, uv.y, 1.0, 1.0);
  }
""".trimIndent()

@Preview
@Composable
fun BasicShaderPlayground() {
  val basicShader = remember { RuntimeShader(shader) }
  ShaderPlaygroundTheme {
    SimpleSketchWithCache(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
    ) {
      with(basicShader) {
        setFloatUniform(
          "resolution",
          size.width,
          size.height
        )
      }
      onDrawBehind {
        drawRect(brush = ShaderBrush(basicShader))
      }
    }
  }
}