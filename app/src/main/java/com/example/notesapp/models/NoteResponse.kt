package com.example.notesapp.models

data class NoteResponse(
    val __v: Int,
    val _id: String,
    val description: String,
    val title: String,
    val userId: String
)