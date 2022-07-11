package edu.bu.projectportal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.bu.projectportal.ProjectPortalApplication
import edu.bu.projectportal.database.Project
import edu.bu.projectportal.repository.ProjectRepositoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ProjectViewModel(application: Application): AndroidViewModel(application) {
    private val projectRepository: ProjectRepositoryImp =
        (application as ProjectPortalApplication).projectRepository

    // use this curProject to update the detail/edit project fragment
    var curProject: MutableLiveData<Project> = MutableLiveData()
    val projects: LiveData<List<Project>>
        get() = projectRepository.projects

    private var allProjectsLiveData:MutableLiveData<List<Project>> = MutableLiveData()

    val allProjects: LiveData<List<Project>>
        get() = allProjectsLiveData

    fun initCurProject(project: Project){
        if (curProject.value == null)
            curProject.value = project
    }

    fun setCurProject(project: Project){
        curProject.value = project
    }

    fun isCurProject(project:Project):Boolean{
        return curProject.value?.id == project.id
    }

    fun updateCurProject(title:String, desp: String, auth: String, link: String, key: String) {
        curProject?.value?.apply{
            this?.title = title
            this?.description = desp
            this?.author = auth
            this?.link = link
            this?.keyword = key
        }
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.editProject(curProject.value!!)
        }
    }

    fun updateFavorite(fav: Boolean) {
        curProject?.value?.apply{
            this?.favorite = fav
        }
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.editProject(curProject.value!!)
        }
    }

    fun fetchAllProjects() {
        viewModelScope.launch(Dispatchers.Main) {
            projectRepository.fetchAllProjects()
        }
    }

    fun fetchProjectsbyFavorite(fav: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            projectRepository.fetchProjectsbyFavorite(fav)
        }
    }

    fun addProject(project: Project){
        viewModelScope.launch(Dispatchers.Main) {
            projectRepository.addProject(project)
        }
    }
    fun delProject(project: Project){
        viewModelScope.launch(Dispatchers.Main) {
            projectRepository.delProject(project)
        }
    }
}