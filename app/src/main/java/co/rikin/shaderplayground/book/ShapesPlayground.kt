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
import co.rikin.shaderplayground.shaders.Util
import co.rikin.shaderplayground.ui.theme.ShaderPlaygroundTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
val rectangleShader = """
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
    float rect3 = rectFill(uv, 0.35, 0.525, 0.05, 0.05);
    float rect4 = rectFill(uv, 0.35, 0.05, 0.05, 0.525);
    color = half3(rect1 + rect3,  rect2, rect4);

    return half4(color, 1.0);
}
""".trimIndent()


@Preview
@Composable
fun Rectangles() {
  ShaderPlaygroundTheme {

    val shader = remember { RuntimeShader(rectangleShader) }

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

@Language("AGSL")
val circleShader = """
uniform float2 resolution;
uniform float time;

float circle(float2 uv, float2 center, float radius) {
    float distance = distance(uv, center);
    return smoothstep(radius+0.01, radius-0.01, distance);
}

float circle(vec2 uv, float radius) {
    vec2 dist = uv-vec2(0.5);
    return 1.-smoothstep(radius-(radius*0.01),
                         radius+(radius*0.01),
                         dot(dist,dist)*4.0);
}

half4 main(float2 coord) {
    float2 uv = coord.xy / resolution;
//      float radius = distance(uv, vec2(0.5));
//      half3 color = half3(radius);
    
    //exercise 1 - contain the entire circular gradient
//    uv = uv * 2.0;
//    float radius = distance(uv, vec2(1.0));
//    half3 color = half3(radius);    

    // exercise 2 - use the step function
//    float radius = 0.5;
//    half3 color = half3(step(radius, distance(uv, vec2(0.5))));
    
    // exercise 3 - inverse the colors
//    float radius = 0.5;
//    half3 color = half3(1.0 - step(radius, distance(uv, vec2(0.5))));
    
    // exercise 4 - use smoothstep + extract function
//    half3 color = half3(circle(uv, float2(0.5), 0.3));
    
    // exercise 5 - add color to circle
//    half3 color = half3(circle(uv, float2(0.5), 0.3));
//    color = color * half3(1.0, 0.8, 0.6);
    
    // exercise 6 - animate the circle?
//    float radius = sin(10.0 * time) * 0.5;
//    half3 color = half3(circle(uv, float2(0.5), radius));
//    color = color * half3(1.0, 0.8, 0.6);
    
    // exercise 7 - place multiple circles?
//    float radius = 0.25;
//    float circle1 = circle(uv, float2(0.25, 0.5), radius);
//    half3 color = half3(circle1);
//    color = color * half3(1.0, 0.8, 0.6);
//    float circle2 = circle(uv, float2(0.75, 0.5), radius);
//    color += circle2 * half3(0.6, 0.8, 1.0);
    
    // exercise 8 - combining distance fields using differnt functions + operations
    float pct = 0.0;
//    pct = distance(uv, float2(0.4)) + distance(uv, float2(0.6));
//    pct = distance(uv, float2(0.4)) * distance(uv, float2(0.6));
//    pct = min(distance(uv, float2(0.4)), distance(uv, float2(0.6)));
//    pct = max(distance(uv, float2(0.4)), distance(uv, float2(0.6)));
//    pct = pow(distance(uv, float2(0.4)), distance(uv, float2(0.6)));

//    pct = circle(uv, float2(0.4), 0.3) + circle(uv, float2(0.6), 0.3);
//    pct = circle(uv, float2(0.4), 0.3) * circle(uv, float2(0.6), 0.3);
//    pct = min(circle(uv, float2(0.4), 0.3), circle(uv, float2(0.6), 0.3));
    pct = max(circle(uv, float2(0.4), 0.3), circle(uv, float2(0.6), 0.3));
    pct -= min(circle(uv, float2(0.4), 0.3), circle(uv, float2(0.6), 0.3));
//    pct = pow(circle(uv, float2(0.4), 0.3), circle(uv, float2(0.6), 0.3));
//    pct = circle(uv, 0.5);
    half3 color = half3(pct);
    color *= half3(0.6, 0.8, 0.6);
    

    return half4(color, 1.0);
}
""".trimIndent()

@Preview
@Composable
fun Circles() {
  ShaderPlaygroundTheme {

    val shader = remember { RuntimeShader(circleShader) }

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

@Language("AGSL")
val distanceFieldShader = """
uniform float2 resolution;
uniform float time;

$Util

half4 main(float2 coord) {
    float2 uv = coord.xy / resolution;
    uv.x *= resolution.x / resolution.y;
    half3 color = half3(0.0);
    float d = 0.0;
    
    // remap space to -1..1
    uv = uv * 2.0 - 1.0;
    
    // make the distance field
    // distance from the point the (0.3, 0.3) or w/e
    // abs helps create this for all 4 quadrants
    d = length(abs(uv) - 0.3);
//    d = length(min(abs(uv)- 0.3, 0.0));
//    d = length(max(abs(uv)- 0.3, 0.0));
    
    // Visualize the distance field
    color = half3(fract(d*10.0));
//    color = half3(step(0.3, d));
//    color = half3(step(0.3, d) * step(d, 0.4));
//    color = half3(smoothstep(0.3, 0.4, d) * smoothstep(0.6, 0.5, d));
    color *= half3(1.0, 0.8, 0.6);
    return half4(color, 1.0);
}
""".trimIndent()

@Preview
@Composable
fun DistanceFields() {
  ShaderPlaygroundTheme {

    val shader = remember { RuntimeShader(distanceFieldShader) }

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

@Language("AGSL")
val polarShader = """
uniform float2 resolution;
uniform float time;

$Util

half3 palette(float t) {
    half3 a = half3(0.5, 0.5, 0.5);
    half3 b = half3(0.5, 0.5, 0.5);
    half3 c = half3(1.0, 1.0, 1.0);
    half3 d = half3(0.00, 0.33, 0.67);
    
    return a + b * cos(twopi * (c * t + d));
}

half4 main(float2 coord) {
    float2 uv = coord.xy / resolution;
    half3 color = half3(0.0);
    
    float2 pos = float2(0.5) - uv;
    float r = length(pos) * 2.0;
    float a = atan(pos.y, pos.x);
    
    float f = cos(a * 3.0);
//      f = abs(cos(a * 3.0));
//    f = abs(cos(a * 2.5)) * 0.5 + 0.3;
//      f = abs(cos(a * 12.0)* sin(a * 3.0)) * 0.8 + 0.1;
//      f = smoothstep(-0.5, 1.0, cos(a * 10.0)) * 0.2 + 0.5;

    // exercise 1 - animate shapes
    float sides = 5.0 * sin(time * 0.4);
    f = cos(a * sides) * 0.5 + 0.3;
//    f = abs(cos(a * sides)) * 0.5 + 0.3;
    float pct = 1.0 - smoothstep(f, f+0.01, r);
    color = half3(pct) * palette(a * sin(time * 0.4));

    // exercise 2 - combine shaping functions to cut holes (flowers, etc);
//    float circle = 0.2;
//    f = abs(cos(a * 6.0)) * 0.5 + 0.3;
//    float pct = 1.0 - smoothstep(f, f+0.01, r);
//    pct -= 1.0 - smoothstep(circle, circle + 0.01, r);
//    color = half3(pct);
    
    // exercise 3 - use plot to draw just the line
//    f = abs(cos(a * 3.0)) * 0.5 + 0.3;
//    float pct = smoothstep(f-0.02, f, r) - smoothstep(f, f +0.02, r);
//    color = half3(pct);
    
    return half4(color, 1.0);

}
""".trimIndent()

@Preview
@Composable
fun PolarShapes() {
  ShaderPlaygroundTheme {
    val shader = remember { RuntimeShader(polarShader) }

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

@Language("AGSL")
val combiningPowers = """
uniform float2 resolution;
uniform float time;

$Util

float polygon(float2 pos, int corners) {
    float angle = atan(pos.x, pos.y) + pi;
    float radius = twopi / float(corners);
    return cos(floor(.5 + angle/radius) * radius - angle) * length(pos);
}

half4 main(float2 coord) {
    float2 uv = coord.xy / resolution;
    uv.y = 1.0 - uv.y;
    uv.x *= resolution.x / resolution.y;
    half3 color = half3(0.0);
    float d = 0.0;
    
    // remap space to -1..1
    uv = uv * 2.0 - 1.0;
    
    // number of sides
    int N = 6;
    
    // angle + radius of current pixel
    float angle = atan(uv.x, uv.y) + pi;
    float radius = twopi / float(N);
    
    // shaping function to modulate distance
    d = polygon(uv, N);
    
    // exercise 2 - mix distance fields using min + max
    d = min(polygon(uv, 3), polygon(uv, 4)); // this is like union
    d = max(polygon(uv, 3), polygon(uv, 4)); // this is like intersect
    
    color = half3(1.0 - smoothstep(.4, .41, d));
    
    return half4(color, 1.0);
}
""".trimIndent()

@Preview
@Composable
fun CombiningPowers() {
  ShaderPlaygroundTheme {
    val shader = remember { RuntimeShader(combiningPowers) }

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