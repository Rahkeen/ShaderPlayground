package co.rikin.shaderplayground.shaders

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.rikin.shaderplayground.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language


@Language("AGSL")
private val magnifyingShader = """
    uniform shader image;
    uniform float2 size;
    uniform float2 loupeCenter;
    uniform float loupeRadius;
    
    
    float mapRange(float value, float inMin, float inMax, float outMin, float outMax) {
        return ((value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin);
    }
    
    float easeInQuart(float x) {
        return x * x * x * x;
    }
    
    float2 lens_distortion(float2 r, float alpha) {
        return r * (1.0 - alpha * dot(r, r));
    }
    
    float2 zoom_point(float2 uv, float2 point, float zoom) {
        //translate so the point is at the origin, scale, and translate back to the point
        return (uv - point) / zoom + point;
    }
    
    half4 main(float2 coord) {
        // normalize coords
        float2 p = coord.xy / size;
        float px = p.x;
        float py = p.y;
        
        
        // sample original image
        half4 color = image.eval(coord);
      
        // distance pixel is from loupe center
        float d = distance(loupeCenter, p);
        float r = loupeRadius;
        float shadowRadius = 0.1;
        
        // draw magnified content within loupe
        if (d <= r) {
            // how zoomed in
            float offset = 0.10;
            
            // r = 0.18;
            
            float2 distortion = lens_distortion(p - loupeCenter, -20.0);
            // calculate bounds of loupe given center + radius
//            float loupeMinX = loupeCenter.x - r;
//            float loupeMaxX = loupeCenter.x + r;
//            float loupeMinY = loupeCenter.y - r;
//            float loupeMaxY = loupeCenter.y + r;
            
            // pixels within the loupe need to sample across a smaller range of the texture
//            float zoomRangeMinX = loupeMinX + offset;
//            float zoomRangeMaxX = loupeMaxX - offset;
//            float zoomRangeMinY = loupeMinY + offset;
//            float zoomRangeMaxY = loupeMaxY - offset

            float2 zoomed = zoom_point(p + distortion, loupeCenter, 4.0);
            
            
            // calculate new coordinates to sample for zoom effect
//            float zoomPx = mapRange(px, loupeMinX, loupeMaxX, zoomRangeMinX, zoomRangeMaxX);
//            float zoomPy = mapRange(py, loupeMinY, loupeMaxY, zoomRangeMinY, zoomRangeMaxY)

          
            float2 zoomCoord = zoomed * size;
            color = image.eval(zoomCoord);
        } else if (d > r && d <= r + shadowRadius) {
          float distanceFromEdge = d - r;
  
          // Progress is normalized within the [0, 1] range.
          float progress = mapRange(distanceFromEdge, 0, shadowRadius, 1, 0);
  
          // Decay progress really quickly to create a more realistic shadow.
          progress = easeInQuart(progress);
  
          // Finally, do some alpha compositing to blend the black shadow color with the original pixel's color:
          float shadowOpacity = mapRange(progress, 1, 0, 0.2, 0);
          color = mix(color, half4(half3(0), 1), half4(shadowOpacity));
        }
        
        return color;
    }
  """.trimIndent()

@Preview
@Composable
fun MagnifyingPlayground() {
  val scope = rememberCoroutineScope()
  var width by remember { mutableStateOf(0f) }
  var height by remember { mutableStateOf(0f) }

  // These are in UV coords (0 - 1)
  var loupeCenterX by remember { mutableStateOf(0.15f) }
  var loupeCenterY by remember { mutableStateOf(0.85f) }
  val loupeRadius = remember { Animatable(0f) }

  val shader = remember { RuntimeShader(magnifyingShader) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color.Black)
      .padding(32.dp),
    contentAlignment = Alignment.Center
  ) {
    Image(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .graphicsLayer {
          clip = true
          shape = RoundedCornerShape(24.dp)

          shader.setFloatUniform(
            "size",
            width,
            height
          )
          shader.setFloatUniform(
            "loupeCenter",
            loupeCenterX,
            loupeCenterY
          )
          shader.setFloatUniform(
            "loupeRadius",
            loupeRadius.value
          )

          renderEffect = RenderEffect
            .createRuntimeShaderEffect(
              shader,
              "image"
            )
            .asComposeRenderEffect()
        }
        .onSizeChanged { size ->
          width = size.width.toFloat()
          height = size.height.toFloat()
        }
        .clip(RoundedCornerShape(24.dp))
        .pointerInput(Unit) {
          awaitEachGesture {
            val change = awaitFirstDown().position
            scope.launch {
              loupeRadius.animateTo(
                0.2f,
                animationSpec = spring(
                  dampingRatio = Spring.DampingRatioMediumBouncy,
                  stiffness = Spring.StiffnessLow
                )
              )
            }
            loupeCenterX = (change.x / size.width).coerceIn(0f, 1f)
            loupeCenterY = (change.y / size.height).coerceIn(0f, 1f)
            do {
              val touchPosition = awaitPointerEvent().changes.last().position
              loupeCenterX = (touchPosition.x / size.width).coerceIn(0f, 1f)
              loupeCenterY = (touchPosition.y / size.height).coerceIn(0f, 1f)
            } while (awaitPointerEvent().changes.none { it.changedToUp() })
            scope.launch {
              loupeRadius.animateTo(
                0.0f,
                animationSpec = tween(
                  durationMillis = 350,
                  easing = EaseInOut
                )
              )
            }
          }
        },
      painter = painterResource(id = R.drawable.mountains),
      contentDescription = "My cute dog, Sora",
      contentScale = ContentScale.Crop
    )
  }
}