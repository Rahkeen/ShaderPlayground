package co.rikin.shaderplayground.shaders

import org.intellij.lang.annotations.Language

@Language("AGSL")
private val chromaticAbberate = """
  uniform shader composable;
  uniform float2 resolution;
  
  half4 main(float2 coord) {
      return half4(1.0, 0.0, 0.0, 1.0);
  }
""".trimIndent()


