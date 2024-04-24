package com.example.m08_elinwetterman.model

import android.graphics.Bitmap

/**
 * Clase de datos que representa un marcador en la aplicación FlurryMaps.
 * @property owner Propietario del marcador.
 * @property markerId Identificador único del marcador.
 * @property latitude Latitud del marcador.
 * @property longitude Longitud del marcador.
 * @property title Título del marcador.
 * @property snippet Fragmento o descripción del marcador.
 * @property category Categoría del marcador.
 * @property photo Foto asociada al marcador (opcional).
 * @property photoReference Referencia de la foto asociada al marcador (opcional).
 */

data class MarkerFlurry(
    var owner: String?,
    var markerId: String?,
    var latitude: Double,
    var longitude: Double,
    var title: String,
    var snippet: String,
    var category: Category,
    var photo: Bitmap?,
    var photoReference: String?
) {
    /**
     * Constructor secundario que inicializa todas las propiedades con valores predeterminados.
     */

    constructor() : this(
        null,
        null,
        0.0,
        0.0,
        "",
        "",
        Category(""),
        null,
        null
    )

    /**
     * Método para modificar el título del marcador.
     * @param newTitle Nuevo título del marcador.
     */
    fun modificarTitle(newTitle: String) {
        title = newTitle
    }

    /**
     * Método para modificar el fragmento o descripción del marcador.
     * @param newSnippet Nuevo fragmento o descripción del marcador.
     */
    fun modificarSnippet(newSnippet: String) {
        snippet = newSnippet
    }

    /**
     * Método para modificar la foto asociada al marcador.
     * @param newPhoto Nueva foto asociada al marcador.
     */
    fun modificarPhoto(newPhoto: Bitmap) {
        photo = newPhoto
    }

    /**
     * Método para modificar la referencia de la foto asociada al marcador.
     * @param newReference Nueva referencia de la foto asociada al marcador.
     */
    fun modificarPhotoReference(newReference: String) {
        photoReference = newReference
    }

    /**
     * Método para modificar la categoría del marcador.
     * @param newReference Nueva categoría del marcador.
     */
    fun modificarCategoria(newReference: String) {
        category.name = newReference
    }
}