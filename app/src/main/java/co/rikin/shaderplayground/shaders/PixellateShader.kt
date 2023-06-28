package co.rikin.shaderplayground.shaders

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.rikin.shaderplayground.R
import org.intellij.lang.annotations.Language

@Language("AGSL")
val pixellateShader = """
  uniform shader image;
  uniform float2 resolution;
  uniform float pixellate;
  
  half4 main(float2 coord) {
      float2 uv = coord.xy / resolution;
      float2 pixelSize = pixellate / resolution;
      float2 newCoord = (floor(uv / pixelSize) * pixelSize) * resolution;
      half4 color = image.eval(newCoord);
      return color;
  }
""".trimIndent()

@Preview
@Composable
fun PixellateShaderPlayground() {
  val shader = remember { RuntimeShader(pixellateShader) }
  var pixellate by remember { mutableStateOf(20f) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(brush = SolidColor(Color.White))
      .padding(32.dp),
    verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      modifier = Modifier
        .graphicsLayer {

          clip = true

          shader.setFloatUniform(
            "resolution",
            size.width,
            size.height
          )

          shader.setFloatUniform(
            "pixellate",
            pixellate
          )

          renderEffect = RenderEffect
            .createRuntimeShaderEffect(
              shader,
              "image",
            )
            .asComposeRenderEffect()
        }
        .fillMaxWidth()
        .aspectRatio(1f),
      painter = painterResource(id = R.drawable.cool_mountain),
      contentScale = ContentScale.Crop,
      contentDescription = "Mountains"
    )
    Slider(
      value = pixellate,
      onValueChange = { pixellate = it },
      valueRange = 1f..50f
    )
  }
}