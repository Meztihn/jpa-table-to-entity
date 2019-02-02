package meztihn.jpa.convert.entity.transform

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class StringTest : Spek({
    data class TestCase(val input: String, val inLowerCamelCase: String, val inUpperCamelCase: String)

    listOf(
        TestCase("", "", ""),
        TestCase("simple", "simple", "Simple"),
        TestCase("snake_case", "snakeCase", "SnakeCase"),
        TestCase("lowerCamelCase", "lowerCamelCase", "LowerCamelCase"),
        TestCase("UpperCamelCase", "upperCamelCase", "UpperCamelCase")
    ).forEach { case ->
        describe("string case conversion") {
            it("in lower camel case") {
                assertThat(case.input.toLowerCamelCase(), equalTo(case.inLowerCamelCase))
            }

            it("in upper camel case") {
                assertThat(case.input.toUpperCamelCase(), equalTo(case.inUpperCamelCase))
            }
        }
    }
})