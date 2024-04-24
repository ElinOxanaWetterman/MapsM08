package com.example.m08_elinwetterman.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.m08_elinwetterman.Routes
import com.example.m08_elinwetterman.model.Category
import com.example.m08_elinwetterman.model.MarkerFlurry
import com.example.m08_elinwetterman.model.Repository
import com.example.m08_elinwetterman.model.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel para manejar la lógica de la pantalla principal de mapas y la interacción con Firebase.
 */

class MapViewModel : ViewModel() {
    // Propiedades para editar marcadores
    var editedTitle by mutableStateOf("")
        private set
    var editedSnippet by mutableStateOf("")
        private set
    var editedCategoryName by mutableStateOf("")
        private set
    var editedPhoto by mutableStateOf<Bitmap?>(null)
        private set
    var editedProfilePhoto by mutableStateOf<Bitmap?>(null)
        private set
    var showTakePhotoScreen by mutableStateOf(false)
        private set
    var showDialog by mutableStateOf(false)
        private set

    // Métodos para modificar las propiedades de edición
    fun modificarEditedTitle(title: String) {
        editedTitle = title
    }

    fun modificarApellidoState(value: String) {
        _apellidoState.value = value
    }

    fun modificarCiudadState(value: String) {
        _ciudadState.value = value
    }

    fun modificarEditedSnippet(snippet: String) {
        editedSnippet = snippet
    }

    fun modificarEditedProfilePhoto(photo: Bitmap?) {
        editedProfilePhoto = photo
    }

    fun modificarEditedPhoto(photo: Bitmap?) {
        editedPhoto = photo
    }

    fun modificarCategoryName(categoryName: String) {
        editedCategoryName = categoryName
    }

    fun modificarShowTakePhotoScreen(value: Boolean) {
        showTakePhotoScreen = value
    }

    fun modificarShowDialog(value: Boolean) {
        showDialog = value
    }

    // Propiedades para almacenar datos locales
    private val title = mutableStateOf("")
    private val snippet = mutableStateOf("")
    private val selectedCategoria = mutableStateOf<Category?>(null)
    private val photoBitmap = mutableStateOf<Bitmap?>(null)
    private val _uriFoto = mutableStateOf<Uri?>(null)
    private var uriFoto = _uriFoto
    private val photoTaken = mutableStateOf(false)
    private val _show = MutableLiveData<Boolean>(false)
    val show = _show
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPositionGranted = _cameraPermissionGranted
    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale
    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied
    private val database = FirebaseFirestore.getInstance()
    private val _markers = MutableLiveData<MutableList<MarkerFlurry>>()
    val markers: LiveData<MutableList<MarkerFlurry>> = _markers
    private var repository: Repository = Repository()
    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied() {
        _showPermissionDenied
    }

    fun modifyTitle(newValue: String) {
        title.value = newValue
    }

    fun getTitle(): String {
        return title.value
    }

    fun modifySnippet(newValue: String) {
        snippet.value = newValue
    }

    fun getSnippet(): String {
        return snippet.value
    }

    fun modifySelectedCategory(newValue: Category?) {
        selectedCategoria.value = newValue
    }

    fun getSelectedCategory(): Category? {
        return selectedCategoria.value
    }

    fun modifyUriPhoto(newValue: Uri?) {
        _uriFoto.value = newValue
    }

    fun modifyPhotoBitmap(newValue: Bitmap?) {
        photoBitmap.value = newValue
    }

    fun getPhotoBitmap(): Bitmap? {
        return photoBitmap.value
    }

    fun modifyPhotoTaken(newValue: Boolean) {
        photoTaken.value = newValue
    }

    fun getPhotoTaken(): Boolean {
        return photoTaken.value
    }

    fun modifyShow(newValue: Boolean) {
        show.value = newValue
    }

    private var expanded by mutableStateOf(false)
    fun modifyExpanded(valorNuevo: Boolean) {
        expanded = valorNuevo
    }

    fun pillarExpanded(): Boolean {
        return expanded
    }

    private var expandedMapa by mutableStateOf(false)
    fun modifyExpandedMapa(valorNuevo: Boolean) {
        expandedMapa = valorNuevo
    }

    fun pillarExpandedMapa(): Boolean {
        return expandedMapa
    }

    private var position = LatLng(41.4534265, 2.1837151)
    fun changePosition(positionNueva: LatLng) {
        position = positionNueva
    }

    fun getPosition(): LatLng {
        return position
    }

    private var _editingPosition = MutableLiveData<LatLng?>()
    fun modificarEditingPosition(newValue: LatLng?) {
        _editingPosition.value = newValue
    }

    fun pillarEditingPosition(): LatLng? {
        return _editingPosition.value
    }

    private val _editingMarkers = MutableLiveData<MarkerFlurry>()
    var editingMarkers: LiveData<MarkerFlurry> = _editingMarkers
    fun setEditingMarkers(marker: MarkerFlurry) {
        _editingMarkers.value = marker
    }

    private val _categories = MutableLiveData<MutableList<Category>>()
    val categories: LiveData<MutableList<Category>> = _categories
    private val _textoDropDown = MutableLiveData<String>()
    val textoDropdown: LiveData<String> = _textoDropDown
    private val _textoDropDownCategorias = MutableLiveData<String>()
    val textoDropdownCategoria: LiveData<String> = _textoDropDownCategorias
    private val _showBottomSheet = MutableLiveData<Boolean>()
    val showBottomSheet = _showBottomSheet
    fun modificarShowBottomSheet(nuevoBoolean: Boolean) {
        _showBottomSheet.value = nuevoBoolean
    }

    fun modificarTextoDropdown(nuevoTexto: String) {
        _textoDropDown.value = nuevoTexto
    }

    fun modificarTextoDropdownCat(nuevoTexto: String) {
        _textoDropDownCategorias.value = nuevoTexto
    }

    init {
        if (_categories.value == null) {
            _categories.value = mutableListOf(
                Category("Shcool"),
                Category("Veterinario"),
                Category("Hospital"),
                Category("Mercadona"),
                Category("Favorites")
            )
        }
    }

    fun getCategories(): List<Category> {
        return _categories.value.orEmpty()
    }

    fun deleteMarker(markerId: String) {
        database.collection("markers").document(markerId).delete()
    }

    fun updateMarker(editedMarker: MarkerFlurry) {
        if (uriFoto.value != null) {
            modifyLoadingMarkers(false)
            val oldImageUrl = editedMarker.photoReference
            uploadImage(uriFoto.value!!) { downloadUrl ->
                editedMarker.modificarPhotoReference(downloadUrl)
                database.collection("markers").document(editedMarker.markerId!!)
                    .set(
                        hashMapOf(
                            "owner" to _loggedUser.value,
                            "positionLatitude" to editedMarker.latitude,
                            "positionLongitude" to editedMarker.longitude,
                            "title" to editedMarker.title,
                            "snippet" to editedMarker.snippet,
                            "categoryName" to editedMarker.category.name,
                            "linkImage" to editedMarker.photoReference
                        )
                    )
                    .addOnSuccessListener {
                        Log.d("Success", ("Marker añadido"))
                        if (oldImageUrl != null) {
                            deleteProfileImage(oldImageUrl)
                        }
                        pillarTodosMarkers()
                        modifyLoadingMarkers(true)

                    }
                    .addOnFailureListener { e ->
                        Log.d(
                            "Error",
                            ("Error: ${e.message}")
                        )
                        modifyLoadingMarkers(true)
                    }

            }
        } else {
            database.collection("markers").document(editedMarker.markerId!!)
                .set(
                    hashMapOf(
                        "owner" to _loggedUser.value,
                        "positionLatitude" to editedMarker.latitude,
                        "positionLongitude" to editedMarker.longitude,
                        "title" to editedMarker.title,
                        "snippet" to editedMarker.snippet,
                        "categoryName" to editedMarker.category.name,
                        "linkImage" to editedMarker.photoReference
                    )
                )
                .addOnSuccessListener {
                    Log.d("Success", ("Marker añadido"))
                    pillarTodosMarkers()
                    modifyLoadingMarkers(true)
                }
                .addOnFailureListener { e ->
                    Log.d("Error", ("Error al añadir: ${e.message}"))
                    modifyLoadingMarkers(true)
                }
        }
    }

    fun addMarkerToDatabase(marker: MarkerFlurry) {
        uploadImage(uriFoto.value!!) { downloadUrl ->
            marker.modificarPhotoReference(downloadUrl)
            database.collection("markers")
                .add(
                    hashMapOf(
                        "owner" to _loggedUser.value,
                        "positionLatitude" to marker.latitude,
                        "positionLongitude" to marker.longitude,
                        "title" to marker.title,
                        "snippet" to marker.snippet,
                        "categoryName" to marker.category.name,
                        "linkImage" to marker.photoReference
                    )
                )
                .addOnSuccessListener {
                    Log.d("Success", ("Marker añadido"))
                    pillarTodosMarkers()
                    modifyLoadingMarkers(true)
                }
                .addOnFailureListener { e ->
                    Log.d("Error", ("Error: ${e.message}"))
                }
        }
    }

    private fun uploadImage(imageUri: Uri, onComplete: (String) -> Unit) {
        Log.d("Inicio", (""))
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")
        modifyLoadingMarkers(false)

        storage.putFile(imageUri)
            .addOnSuccessListener { uploadTask ->
                Log.i("UPLOAD", "Image cargada")
                Log.d("Success", (""))
                uploadTask.storage.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Log.d("Success", ("URL imagen: $downloadUrl"))
                    onComplete(downloadUrl)
                }
            }
            .addOnFailureListener {
                Log.i("UPLOAD", "Image ERROR")
                Log.d("Error", (""))
                modifyLoadingMarkers(true)
            }
    }

    fun pillarTodosMarkers() {
        repository.getMarkers()
            .whereEqualTo("owner", _loggedUser.value)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                }
                val tempList = mutableListOf<MarkerFlurry>()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val newMarker = dc.document.toObject(MarkerFlurry::class.java)
                        newMarker.markerId = dc.document.id
                        newMarker.latitude =
                            dc.document.get("positionLatitude").toString().toDouble()
                        newMarker.longitude =
                            dc.document.get("positionLongitude").toString().toDouble()
                        newMarker.category.name = dc.document.get("categoryName").toString()
                        newMarker.photoReference = dc.document.get("linkImage").toString()
                        tempList.add(newMarker)
                        Log.d("Success", ("Adios :( $newMarker"))
                    }

                }
                _markers.value = tempList
            }
    }

    fun pillarTodosMarkersCategoria(categoria: String) {
        repository.getMarkers()
            .whereEqualTo("owner", _loggedUser.value)
            .whereEqualTo("categoryName", categoria)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                }
                val tempList = mutableListOf<MarkerFlurry>()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val newMarker = dc.document.toObject(MarkerFlurry::class.java)
                        newMarker.markerId = dc.document.id
                        newMarker.latitude =
                            dc.document.get("positionLatitude").toString().toDouble()
                        newMarker.longitude =
                            dc.document.get("positionLongitude").toString().toDouble()
                        newMarker.category.name = dc.document.get("categoryName").toString()
                        newMarker.photoReference = dc.document.get("linkImage").toString()
                        tempList.add(newMarker)
                        Log.d("Success", ("Adios :( " + newMarker.category.name))
                    }

                }
                _markers.value = tempList
            }
    }

    private val auth = FirebaseAuth.getInstance()
    fun userLogged(): Boolean {
        return auth.currentUser != null
    }

    private val _goToNext = MutableLiveData<Boolean>()
    val goToNext = _goToNext
    private val _userId = MutableLiveData<String>()
    private val _loggedUser = MutableLiveData<String>()
    val loggedUser = _loggedUser
    fun pillarLoggedUser(): String {
        return _loggedUser.value.toString()
    }

    fun modificarLoggedUser(nuevo: String) {
        _loggedUser.value = nuevo
    }

    private val _isLoading = MutableLiveData(true)
    val isLoading = _isLoading
    private val _isLoadingMarkers = MutableLiveData(true)
    val isLoadingMarkers = _isLoadingMarkers
    fun modifyLoadingMarkers(newValue: Boolean) {
        _isLoadingMarkers.value = newValue
    }

    private val _emailState = MutableLiveData<String>()
    val emailState: LiveData<String> = _emailState
    private val _passwordState = MutableLiveData<String>()
    val passwordState: LiveData<String> = _passwordState
    private val _nombreState = MutableLiveData<String>()
    val nombreState: LiveData<String> = _nombreState
    private val _apellidoState = MutableLiveData<String>()
    val apellidoState: LiveData<String> = _apellidoState
    private val _ciudadState = MutableLiveData<String>()
    val ciudadState: LiveData<String> = _ciudadState
    private val _showDialogPass = MutableLiveData<Boolean>()
    val showDialogPass: LiveData<Boolean> = _showDialogPass
    private val _passwordProblem = MutableLiveData<Boolean>()
    val passwordProblem: LiveData<Boolean> = _passwordProblem
    fun modificarEmailState(value: String) {
        _emailState.value = value
    }

    fun modificarPasswordState(value: String) {
        _passwordState.value = value
    }

    fun modificarNombreState(value: String) {
        _nombreState.value = value
    }

    fun modificarShowDialogPass(value: Boolean) {
        _showDialogPass.value = value
    }

    fun modificarPasswordProblem(value: Boolean) {
        _passwordProblem.value = value
    }

    fun modifyProcessing(newValue: Boolean) {
        _isLoading.value = newValue
    }

    private val _showDialogAuth = MutableLiveData<Boolean>()
    val showDialogAuth: LiveData<Boolean> = _showDialogAuth
    fun modificarShowDialogAuth(value: Boolean) {
        _showDialogAuth.value = value
    }

    private val _emailDuplicated = MutableLiveData<Boolean>()
    val emailDuplicated: LiveData<Boolean> = _emailDuplicated


    fun register(context: Context, username: String, password: String) {
        val userPrefs = User(context)
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email
                    _goToNext.value = true
                    modifyProcessing(false)
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(_emailState.value!!, _passwordState.value!!)
                    }
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                database.collection("user")
                                    .add(
                                        hashMapOf(
                                            "owner" to _loggedUser.value,
                                            "name" to _nombreState.value,
                                            "apellido" to _apellidoState.value,
                                            "ciudad" to _ciudadState.value,
                                        )
                                    )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error creating user : ${task.exception}")
                    modifyProcessing(true)
                    _emailDuplicated.value = true
                    _showDialogAuth.value = true
                }
            }
    }

    private val _validLogin = MutableLiveData<Boolean>()
    val validLogin: LiveData<Boolean> = _validLogin
    private val _passwordVisibility = MutableLiveData<Boolean>()
    val passwordVisibility = _passwordVisibility
    fun cambiarPassVisibility(nuevoBoolean: Boolean) {
        _passwordVisibility.value = nuevoBoolean
    }

    private val _permanecerLogged = MutableLiveData<Boolean>()
    val permanecerLogged = _permanecerLogged
    fun cambiarPermanecerLogged(nuevoBoolean: Boolean) {
        _permanecerLogged.value = nuevoBoolean
    }

    fun login(username: String?, password: String?) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email
                    _goToNext.value = true
                    modifyProcessing(false)
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                database.collection("user")
                                    .add(
                                        hashMapOf(
                                            "owner" to _loggedUser.value,
                                            "name" to _nombreState.value,
                                            "apellido" to _apellidoState.value,
                                            "ciudad" to _ciudadState.value,
                                        )
                                    )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error signing in: ${task.exception}")
                    modifyProcessing(true)
                    _emailDuplicated.value = false
                    _showDialogAuth.value = true
                }
            }
            .addOnFailureListener {
                _validLogin.value = false
            }
    }

    fun signOut(context: Context, navController: NavController) {
        val userPrefs = User(context)
        if (_permanecerLogged.value == true) {
            CoroutineScope(Dispatchers.IO).launch {
                println("JEJE ESTOY EN TRUE")
                userPrefs.deleteUserPass()
            }
        } else {
            modificarEmailState("")
            CoroutineScope(Dispatchers.IO).launch {
                userPrefs.deleteUserData()
            }
        }
        auth.signOut()
        _goToNext.value = false
        _passwordState.value = ""

        modifyProcessing(true)
        navController.navigate(Routes.LogScreen.route)
    }

    private val _imageUrlForUser = MutableLiveData<String?>()
    val imageUrlForUser = _imageUrlForUser
    private val _nombreUsuario = MutableLiveData<String>()
    val nombreUsuario = _nombreUsuario
    fun getProfileImageUrlForUser() {
        repository.getUserImageUri()
            .whereEqualTo("owner", _loggedUser.value)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore no response", error.message.toString())
                    return@addSnapshotListener
                }

                var tempString: String? = null

                var tempStringNombre = "¿?"
                if (value != null) {
                    for (dc: DocumentChange in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            tempString = dc.document.getString("image") ?: tempString
                            tempStringNombre =
                                (dc.document.getString("name") ?: tempString).toString()
                            Log.d("Success", ("Successful"))
                            Log.d("Success", (tempString.toString()))
                        }
                    }
                }
                _nombreUsuario.value = tempStringNombre
                _imageUrlForUser.value = tempString
            }
    }

    fun updateUser() {
        getProfileImageUrlForUser()
        var oldImageUrl: String? = null
        if (_imageUrlForUser.value != null) {
            oldImageUrl = _imageUrlForUser.value
        }
        uploadImage(uriFoto.value!!) { downloadUrl ->
            database.collection("user")
                .whereEqualTo("owner", _loggedUser.value)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val data = mutableMapOf<String, Any>("image" to downloadUrl)
                        document.reference.update(data)
                            .addOnSuccessListener {
                                Log.d(
                                    "Success",
                                    ("Usuario actualizado")
                                )


                                if (oldImageUrl != null) {
                                    deleteProfileImage(oldImageUrl)
                                }
                                modifyLoadingMarkers(true)
                            }
                            .addOnFailureListener { e ->
                                Log.d(
                                    "Error",
                                    ("Error: ${e.message}")
                                )
                            }
                        modifyLoadingMarkers(true)
                    }
                    getProfileImageUrlForUser()
                }
                .addOnFailureListener { exception ->
                    Log.d("Error", ("Error: ${exception.message}"))
                    modifyLoadingMarkers(true)
                }
        }
    }

    private fun deleteProfileImage(imageUrl: String) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.storage.getReferenceFromUrl(imageUrl)
        imageRef.delete()
            .addOnSuccessListener {
                Log.d(
                    "Success",
                    ("eliminado correctamente del almacenamiento")
                )
            }
            .addOnFailureListener { e ->
                Log.d(
                    "Error",
                    ("Error: ${e.message}")
                )
            }
    }

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            modifyProcessing(false)
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Success", "Log :D")
                            val userRef =
                                database.collection("user").whereEqualTo("owner", _loggedUser.value)
                            userRef.get()
                                .addOnSuccessListener { documents ->
                                    if (documents.isEmpty) {
                                        database.collection("user")
                                            .add(
                                                hashMapOf(
                                                    "owner" to _loggedUser.value,
                                                    "name" to (_loggedUser.value?.split("@")?.get(0)
                                                        ?: ""),
                                                )
                                            )
                                    }
                                }
                            home()
                        }
                    }
                    .addOnFailureListener {
                        Log.d(
                            "Error",
                            "Fallo"
                        )
                    }
            } catch (ex: Exception) {
                Log.d(
                    "google",
                    "google" + ex.localizedMessage
                )
            }
        }
}