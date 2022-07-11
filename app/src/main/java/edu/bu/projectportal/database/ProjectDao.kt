package edu.bu.projectportal.database

import androidx.room.*

@Dao
interface ProjectDao {
//    @Insert
//    fun addProjects(projects:List<Project>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProject(project:Project)

    @Delete
    suspend fun delProject(project:Project)

    @Query("SELECT * FROM projects WHERE favorite = :projFavorite")
    suspend fun fetchPojectsbyFavorite(projFavorite: Boolean): List<Project>

    @Update
    suspend fun editProject(project: Project)

    @Query("SELECT * FROM projects")
    suspend fun fetchAllProjects(): List<Project>
}