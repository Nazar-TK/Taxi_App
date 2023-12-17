package com.example.taxiapp.di

import com.example.taxiapp.core.Constants
import com.example.taxiapp.data.DriverRepositoryImpl
import com.example.taxiapp.data.PassengerRepositoryImpl
import com.example.taxiapp.domain.repository.DriverRepository
import com.example.taxiapp.domain.repository.PassengerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaxiModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }


    @Provides
    @Singleton
    fun provideRealtimeDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance(Constants.DATABASE_URL)
    }

    @Provides
    @Singleton
    fun provideStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().getReference(Constants.USERS_AVATARS)
    }

//    @Provides
//    @Singleton
//    fun provideDriversDatabaseReference(db: FirebaseDatabase): DatabaseReference {
//        return db.reference.child("drivers")
//    }
//
//    @Provides
//    @Singleton
//    fun providePassengerDatabaseReference(db: FirebaseDatabase): DatabaseReference {
//        return db.reference.child("passengers")
//    }

    @Provides
    @Singleton
    fun provideDriverRepository(
        firebaseAuth: FirebaseAuth,
        databaseRef : FirebaseDatabase,
        storageRef: StorageReference
    ): DriverRepository {
        return DriverRepositoryImpl(firebaseAuth, databaseRef, storageRef)
    }

    @Provides
    @Singleton
    fun providePassengerRepository(
        firebaseAuth: FirebaseAuth,
        databaseRef : FirebaseDatabase,
        storageRef: StorageReference
    ): PassengerRepository {
        return PassengerRepositoryImpl(firebaseAuth, databaseRef, storageRef)
    }
}