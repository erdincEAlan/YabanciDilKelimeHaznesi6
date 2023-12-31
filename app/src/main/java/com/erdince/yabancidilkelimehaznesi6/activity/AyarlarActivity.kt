

package com.erdince.yabancidilkelimehaznesi6.activity

import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation


@Suppress("OverrideDeprecatedMigration", "OverrideDeprecatedMigration")

class AyarlarActivity : AppCompatActivity() {

    private var pickFileButton: Button? = null
    private var saveButton: ImageButton? = null
    private var ayarlarBackButton: ImageButton? = null
    private var signOutButton: ImageButton? = null
    private var newUserNameEditText: EditText? = null
    private var newPassEditText: EditText? = null
    private var profilePhoto: ImageView? = null
    private var photoUploadButton: Button? = null
    private var yeniKullaniciAdiTextView : TextView? =null
    private var yeniSifreTextView : TextView? =null

    var profilePhotoUploadTask: UploadTask? = null
    var downloadUri: Uri? = null
    var imagesRef: StorageReference? = null
    var profilePhotoRef: StorageReference? = null
    var profileUpdates: UserProfileChangeRequest? = null

    var kullaniciAdi: String? = null
    var kullanici: MutableMap<String, Any>? = null
    var fileUri: Uri? = null
    val db = Firebase.firestore
    var user = Firebase.auth.currentUser
    val auth = Firebase.auth
    var photoUserUri = user?.photoUrl
    val storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    val uid = user?.uid.toString()
    var uri: Uri? = null
    var yeniSifre: String? = null
    val userDatabaseRef = db.collection("user").document(user!!.uid)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayarlar)
        init()
    }

    private fun init() {
        setDatabaseReferences()
        takeUserElements()
        initUI()
        setButtonClickers()

    }

    private fun initUI() {
        setUI()
        setProfilePhoto()
    }

    private fun setButtonClickers() {

        signOutButton?.setOnClickListener() {
            signOut()
        }

        ayarlarBackButton?.setOnClickListener() {
        }


        saveButton?.setOnClickListener() {
            takeTextAndUpdateUser()
            checkAndUpdateUserPassword()
        }

        pickFileButton?.setOnClickListener() {
            pickFile()

        }
        photoUploadButton?.setOnClickListener() {

            uploadAndUpdatePhoto()

        }
    }


    private fun updateFirebaseProfile() {
        setProfileUpdates()
        user?.updateProfile(profileUpdates!!)

    }



    private fun updateUserDatabaseDocument() {
        userDatabaseRef.update("kullaniciAdi", kullaniciAdi)
    }

    private fun uploadAndUpdatePhoto() {

        uploadFile()
        makeToast("Fotoğraf yükleniyor lütfen bekleyin")
        takeDownloadUriAndUpdateProfilePhoto()
    }

    private fun takeDownloadUriAndUpdateProfilePhoto() {
        val urlTask = profilePhotoUploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            profilePhotoRef?.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUri = task.result
                updateUserProfilePhoto()
            }
        }?.addOnFailureListener() {
            makeToast("İşlem sırasında bir sorun oluştu. Lütfen internet bağlantınızı kontrol edin")
        }
    }

    private fun uploadFile() {
        fileUri = uri
        profilePhotoUploadTask = profilePhotoRef?.putFile(fileUri!!)
    }

    private fun updateUserProfilePhoto() {
        updateFirebaseProfile()
        makeToast("Profil Fotoğrafı başarıyla güncellendi")
    }

    private fun pickFile() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Dosya secin"), 111)
    }

    private fun checkAndUpdateUserPassword() {
        if (yeniSifre != null && yeniSifre != "") {
            updateUserPassword()
            signOut()
        }
    }

    private fun updateUserPassword() {
        user?.updatePassword(newPassEditText?.text.toString())
        makeToast("Yeni şifrenizle tekrar giriş yapın")
    }

    private fun signOut() {
        Firebase.auth.signOut()
        finish()
    }

    private fun takeTextAndUpdateUser() {
        setStringsFromEditText()
        updateUserDatabaseDocument()
        updateFirebaseProfile()
        makeToast("Yeni bilgiler kaydedildi")
    }


    private fun setProfileUpdates() {
        profileUpdates = userProfileChangeRequest {
            displayName = kullaniciAdi
            if (downloadUri!=null){
                photoUri = downloadUri
            }

        }

    }

    private fun setStringsFromEditText() {
        yeniSifre = newPassEditText?.text.toString()
        kullaniciAdi = newUserNameEditText?.text.toString()
    }


    private fun takeUserElements() {
        takeUserDatabaseDocumentAndSet()
    }

    private fun setUserDatabaseReferences() {

        profilePhotoRef = storageRef.child("images/" + uid + ".jpg")
    }

    private fun setDatabaseReferences() {
        setFireStorageDatabaseReferences()
        setUserDatabaseReferences()
    }

    private fun setFireStorageDatabaseReferences() {
        imagesRef = storageRef.child("images")
    }

    private fun takeUserDatabaseDocumentAndSet() {
        userDatabaseRef.get().addOnSuccessListener { user ->
            kullanici = user.data as MutableMap<String, Any>
            checkAndSetTextVisibility()
            setTexts()
        }
    }

    private fun checkAndSetTextVisibility() {
        if (kullanici!!["accountType"].toString() != "appAccount") {
            newPassEditText?.visibility = View.GONE
            newUserNameEditText?.visibility = View.GONE
            yeniSifreTextView?.visibility = View.GONE
            yeniKullaniciAdiTextView?.visibility = View.GONE

        }
    }

    private fun setTexts() {
        newUserNameEditText?.setText(kullanici!!["kullaniciAdi"].toString())
    }

    private fun setProfilePhoto() {

        if (photoUserUri != null) {
            Picasso.get().load(photoUserUri).resize(140, 150).transform(CropCircleTransformation())
                .into(profilePhoto)
        }

    }

    private fun setUI() {
        pickFileButton = findViewById(R.id.fileButton)
        saveButton = findViewById(R.id.saveButton)
        ayarlarBackButton = findViewById(R.id.backButton)
        signOutButton = findViewById(R.id.logOutButton)
        newUserNameEditText = findViewById(R.id.newUsernameEditText)
      //  newPassEditText = findViewById(R.id.ayarlarYeniSifreEditText)
        profilePhoto = findViewById(R.id.ayarlarProfilePhoto)
        photoUploadButton = findViewById(R.id.uploadButton)
       // yeniSifreTextView = findViewById(R.id.ayarlarYeniSifreTxt)
        yeniKullaniciAdiTextView = findViewById(R.id.ayarlarYeniKullanıcıAdiTxt)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            uri = data?.data
            makeToast("Fotoğraf Seçildi")

        }
    }

}