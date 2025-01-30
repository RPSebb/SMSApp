package com.example.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.io.OutputStream

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Table(val name: String)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Field(val name: String, val type: String)

class ModelProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val packageName = "org.models.generated"
    private val fileName = "ModelFactory"

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val tables = resolver.getSymbolsWithAnnotation("com.example.processor.Table")
        if(tables.count() < 1) { return emptyList() }

        val file = createFile(resolver)
        var createFromCursor = ""
        var getColumns = ""
        var getTable = ""
        var toString = ""
        var imports = "import android.database.Cursor\n"

        tables.filterIsInstance<KSClassDeclaration>().forEach { table ->

            val className = table.simpleName.asString()
            val packageName = table.packageName.asString()
            var getTableParameters = ""
            var toStringParameters = ""
            var constructorParameters = ""
            var getColumnsParameters = ""
            imports += "import $packageName.$className\n"

            table.getAnnotationsByType(Table::class).forEach { getTableParameters += it.name }
            table.getAllProperties().forEach {
                val name = it.simpleName.getShortName()
                it.getAnnotationsByType(Field::class).forEach {
                    getColumnsParameters += "\"${it.name}\","
                    toStringParameters += "\t\t\t\t\"$name :\" + instance.${name}.toString() + \"\\n\" +\n"
                    constructorParameters += "\t\t\t\tcursor.get${it.type}(cursor.getColumnIndexOrThrow(\"${it.name}\")),\n"
                }
            }
            getTable += "\t\t\t${className}::class -> {${getTableParameters}}\n"
            toString += "\t\t\t${className}::class -> {\n\t\t\t\tval instance = obj as ${className}\n${toStringParameters.dropLast(2)}\n\t\t\t}\n"
            getColumns += "\t\t\t${className}::class -> arrayOf(${getColumnsParameters.dropLast(1)})\n"
            createFromCursor += "\t\t\t${className}::class -> ${className}(\n${constructorParameters.dropLast(2)}\n\t\t\t) as T\n"
        }
                val template =
"""
package $packageName
$imports
object $fileName {
    fun toString(obj: Any): String {
        return when (obj::class) {
$toString
            else -> throw IllegalArgumentException("Unknown class")
        }
    }
    
    inline fun <reified T> createFromCursor(cursor: Cursor): T {
        return when (T::class) {
$createFromCursor
            else -> throw IllegalArgumentException("Unknown class")
        }
    }
    
    inline fun <reified T> getTable(): android.net.Uri {
        return when (T::class) {
$getTable
            else -> throw IllegalArgumentException("Unknown class")
        }
    }
    
    inline fun <reified T> getColumns(): Array<String> {
        return when (T::class) {
$getColumns
            else -> throw IllegalArgumentException("Unknown class")
        }
    }
}""".trimIndent()
        file.bufferedWriter().use { it.write(template) }
        return emptyList()
    }

    private fun createFile(resolver: Resolver): OutputStream {
        return codeGenerator.createNewFile(
            dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
            packageName = packageName,
            fileName = fileName
        )
    }
}

class ModelProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ModelProcessor(environment)
    }
}