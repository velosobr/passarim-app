// Testes da forma cookie: o contorno fica dentro da área de quem ela recorta
// e a ocupa quase toda (o cookie de 9 ondas não é simétrico na horizontal,
// então sobra uma folga de uns 2% dos lados), e duas formas com as mesmas ondas são iguais. Rodam com o
// Robolectric porque o Path do Compose, no Android, é o android.graphics.Path,
// que só existe num Android (de verdade ou imitado).

package com.velosobr.piuds

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isLessThanOrEqualTo
import assertk.assertions.isNotEqualTo
import com.velosobr.piuds.components.PiuCookieShape
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
// NATIVE: o Robolectric usa a biblioteca gráfica de verdade, e o Path calcula os limites.
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PiuCookieShapeTest {
    @Test
    fun `o contorno fica dentro da area e a ocupa quase toda`() {
        val outline = PiuCookieShape().createOutline(Size(120f, 80f), LayoutDirection.Ltr, Density(1f))
        assertThat(outline).isInstanceOf<Outline.Generic>()
        val bounds = (outline as Outline.Generic).path.getBounds()
        assertThat(bounds.left).isGreaterThanOrEqualTo(-0.5f)
        assertThat(bounds.top).isGreaterThanOrEqualTo(-0.5f)
        assertThat(bounds.right).isLessThanOrEqualTo(120.5f)
        assertThat(bounds.bottom).isLessThanOrEqualTo(80.5f)
        assertThat(bounds.width).isGreaterThanOrEqualTo(120f * 0.95f)
        assertThat(bounds.height).isGreaterThanOrEqualTo(80f * 0.95f)
    }

    @Test
    fun `formas com as mesmas ondas sao iguais`() {
        assertThat(PiuCookieShape()).isEqualTo(PiuCookieShape(lobes = 9))
        assertThat(PiuCookieShape().hashCode()).isEqualTo(PiuCookieShape(lobes = 9).hashCode())
        assertThat(PiuCookieShape(lobes = 9)).isNotEqualTo(PiuCookieShape(lobes = 12))
    }
}
