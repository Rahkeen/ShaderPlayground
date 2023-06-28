package co.rikin.shaderplayground.shaders

import android.graphics.RuntimeShader
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.rikin.shaderplayground.SimpleSketchWithCache
import co.rikin.shaderplayground.ui.theme.Darkness
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

@Preview
@Composable
fun GradientHelp() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color.LightGray)
  ) {
    Box(modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .drawWithCache {
        onDrawWithContent {
          drawRect(
            brush = Brush.verticalGradient(
              0f to Color.White,
              1f to Color.Transparent,
              startY = 0f,
              endY = 60.dp.toPx()
            ),
            topLeft = Offset.Zero,
            size = Size(this.size.width, 60.dp.toPx())
          )
        }
      })
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .height(60.dp)
        .background(
          brush = Brush.verticalGradient(
            0f to Color.Transparent,
            1f to Color.White
          )
        )
    )
  }
}