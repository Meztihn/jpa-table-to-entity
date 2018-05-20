package meztihn.jpa.convert.entity.transform

import meztihn.jpa.convert.entity.java.Constructor
import meztihn.jpa.convert.entity.java.Explicitness
import meztihn.jpa.convert.entity.java.Mutability

data class Options(
    val mutability: Mutability,
    val constructor: Constructor,
    val namesExplicitness: Explicitness,
    val indent: String
)