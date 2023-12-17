package com.example.taxiapp.di

import com.example.taxiapp.data.DriverRepositoryImpl
import com.example.taxiapp.data.PassengerRepositoryImpl
import com.example.taxiapp.domain.repository.DriverRepository
import com.example.taxiapp.domain.repository.PassengerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
        return FirebaseDatabase.getInstance("https://taxiapp-99fcc-default-rtdb.firebaseio.com")
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
        firebaseRef : DatabaseReference
    ): DriverRepository {
        return DriverRepositoryImpl(firebaseAuth, firebaseRef)
    }

    @Provides
    @Singleton
    fun providePassengerRepository(
        firebaseAuth: FirebaseAuth,
        databaseRef : FirebaseDatabase
    ): PassengerRepository {
        return PassengerRepositoryImpl(firebaseAuth, databaseRef)
    }
}