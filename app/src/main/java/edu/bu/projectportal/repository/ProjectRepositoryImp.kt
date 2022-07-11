package edu.bu.projectportal.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.bu.projectportal.database.Project
import edu.bu.projectportal.database.ProjectDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService


class ProjectRepositoryImp(
    projectDao1: ExecutorService,
    private val projectDao: ProjectDao
) : ProjectRepository {

    private val projectsLiveData = MutableLiveData<List<Project>>()

    val projects:LiveData<List<Project>>
        get() = projectsLiveData


    override suspend fun addProject(project: Project){
        GlobalScope.launch(Dispatchers.Main) {
            projectDao.addProject(project)
        }
    }

    override suspend fun delProject(project: Project) {
        GlobalScope.launch(Dispatchers.Main) {
            projectDao.delProject(project)
        }
    }

    override suspend fun editProject(project: Project) {
        GlobalScope.launch(Dispatchers.IO) {
            projectDao.editProject(project)
        }
    }

    override suspend fun fetchAllProjects() {
        GlobalScope.launch(Dispatchers.IO) {
            val results = projectDao.fetchAllProjects()
            projectsLiveData.postValue(results)
        }
    }

    override suspend fun fetchProjectsbyFavorite(fav: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            val results = projectDao.fetchPojectsbyFavorite(fav)
            projectsLiveData.postValue(results)
        }
    }
}