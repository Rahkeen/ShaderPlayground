package co.rikin.shaderplayground.book

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
val example1 = """
uniform float2 resolution;
uniform float time;

vec4 rect(float2 uv, float left, float bottom, float right, float top) {
    return step(vec4(left, bottom, right, top), vec4(uv, 1.0 - uv));
}

float rectFill(float2 uv, float left, float bottom, float right, float top) {
    vec4 rect = rect(uv, left, bottom, right, top);
    return rect.x * rect.y * rect.z * rect.w;
}

float rectBorder(float2 uv, float left, float bottom, float right, float top, float border) {
    vec4 outerRect = rect(uv, left, bottom, right, top);
    vec4 innerRect = rect(uv, left + border, bottom + border, right + border, top + border);
    
    float outerColor = outerRect.x * outerRect.y * outerRect.z * outerRect.w;
    float innerColor = innerRect.x * innerRect.y * innerRect.z * innerRect.w;
    
    return outerColor - innerColor;
}


half4 main(float2 fragCoord) {
    float2 uv = fragCoord.xy / resolution;
    uv.y = 1.0 - uv.y;
    
    half3 color = half3(0.0);

//      // part 1
//      float left = step(0.1, uv.x);
//      float bottom = step(0.1, uv.y);
//      
//      // mult is similar to logical AND
//      color = half3(left * bottom);
    
//      // part 2
//      vec2 bottomLeft = step(vec2(0.1), uv);
//      float pct = bottomLeft.x * bottomLeft.y;
//      // top-right by inverting coordinate space (1 - uv);
//      vec2 topRight = step(vec2(0.1), 1.0 - uv);
//      pct *= topRight.x * topRight.y;
//      color = half3(pct);
    
//      // exercise1 - change size of rect
//      vec4 rect = rect(uv, 0.1, 0.2, 0.2, 0.2);
//      float pct = rect.x * rect.y * rect.z * rect.w;
//      color = half3(pct);
    
//    // exercise2 - experiment with smoothstep
//    vec2 bottomLeft = smoothstep(0.05, 0.1, uv);
//    float pct = bottomLeft.x * bottomLeft.y;
//    // top-right by inverting coordinate space (1 - uv);
//    vec2 topRight = smoothstep(0.10, 0.2, 1.0 - uv);
//    pct *= topRight.x * topRight.y;
//    color = half3(pct);

    // exercise 3 - use floor()
//    float left = floor(uv.x + 0.9);
//    float bottom = floor(uv.y + 0.9);
//    float right = floor((1.0 - uv.x) + 0.9);
//    float top = floor(1.0 - uv.y + 0.9);
//    color = half3(left * bottom * right * top);

    // exercise 4 - function that draws outline of rect
//    color = half3(rectBorder(uv, 0.2, 0.2, 0.2, 0.2, 0.02));

    // exercise 5 - draw multiple rects
    float rect1 = rectFill(uv, 0.05, 0.05, 0.85, 0.05);
    float rect2 = rectFill(uv, 0.20, 0.05, 0.7, 0.05);
    float rect3 = rectFill(uv, 0.35, 0.55, 0.05, 0.05);
    float rect4 = rectFill(uv, 0.35, 0.05, 0.05, 0.50);
    color = half3(rect1 + rect2 + rect3 + rect4);

    return half4(color, 1.0);
}
""".trimIndent()


@Preview
@Composable
fun Example1() {
  ShaderPlaygroundTheme {

    val shader = remember { RuntimeShader(example1) }

    SimpleSketchWithCache(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
    ) { time ->
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