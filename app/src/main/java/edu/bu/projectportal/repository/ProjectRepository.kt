package edu.bu.projectportal.repository

import edu.bu.projectportal.database.Project

interface ProjectRepository {

    suspend fun addProject(project: Project)
    suspend fun delProject(project: Project)
    suspend fun editProject(project: Project)
    suspend fun fetchAllProjects()
    suspend fun fetchProjectsbyFavorite(fav: Boolean)
}