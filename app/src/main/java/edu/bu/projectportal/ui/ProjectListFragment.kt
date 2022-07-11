package edu.bu.projectportal.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.bu.projectportal.viewmodel.ProjectViewModel
import edu.bu.projectportal.R
import edu.bu.projectportal.database.Project



/**
 * A fragment representing a list of Items.
 */
class ProjectListFragment : Fragment(), ProjectListRecyclerViewAdapter.OnClickListener {
    // the number of columns to display the list.
    // the default value is 1
    // the value can be changed through the argument passed to the fragment
    private var columnCount = 1
    private lateinit var onClickAddProjListener: OnClickAddProjListener
    private lateinit var adapter: ProjectListRecyclerViewAdapter
    private lateinit var viewModel: ProjectViewModel
    private lateinit var showFavOnly: CheckBox

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnClickAddProjListener) {
            onClickAddProjListener = context
        } else {
            throw RuntimeException("Must implement EditProjectListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project_list, container, false)
   }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        showFavOnly = view.findViewById(R.id.showFav)

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            onClickAddProjListener.onClickAddProj()
        }

        viewModel =
            ViewModelProvider(requireActivity()).get(ProjectViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.projectListView)
        recyclerView.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }

        adapter = ProjectListRecyclerViewAdapter(this)
        recyclerView.adapter = adapter

        if(loadFav()) {
            showFavOnly.isChecked = true;
            viewModel.fetchProjectsbyFavorite(true)
            viewModel.projects.observe(viewLifecycleOwner, Observer {
                adapter.replaceItems(it)
                viewModel.initCurProject(adapter.getProject(0))
            })

        }
        else {
            showFavOnly.isChecked = false;
            viewModel.fetchAllProjects()
            viewModel.projects.observe(viewLifecycleOwner, Observer {
                adapter.replaceItems(it)
                viewModel.initCurProject(adapter.getProject(0))
            })
        }

        showFavOnly.setOnClickListener {
            saveFav(showFavOnly.isChecked)
        }

        viewModel.curProject.observe(viewLifecycleOwner, {
            adapter.notifyDataSetChanged()
        })

        ItemTouchHelper(SwipeToDeleteCallback()).attachToRecyclerView(recyclerView)
    }

    interface OnClickAddProjListener {
        fun onClickAddProj();
    }

    override fun onItemClick(projPos: Int, projId: Long) {
        viewModel.setCurProject(adapter.getProject(projPos))
    }


    inner class SwipeToDeleteCallback: ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) = makeMovementFlags(
            ItemTouchHelper.ACTION_STATE_SWIPE,
            ItemTouchHelper.LEFT
        )

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            val project = adapter.getProject(position)
            if (viewModel.isCurProject(project)) {
                if (position > 0)
                    viewModel.setCurProject(adapter.getProject(position - 1))
                else if (adapter.getItemCount() > 1 )
                    viewModel.setCurProject(adapter.getProject(position + 1))
                else
                    viewModel.setCurProject(Project(0,"No more projects","", "", "", ""))

            }
            viewModel.delProject(project)
        }
    }

    private fun saveFav(fav: Boolean){
        requireActivity().getSharedPreferences("ShowFavOnly", Context.MODE_PRIVATE).edit().putBoolean("ShowFavoriteOnly", fav).commit()
    }

    private fun loadFav():Boolean {
        return requireActivity().getSharedPreferences("ShowFavOnly",
            Context.MODE_PRIVATE).getBoolean("ShowFavoriteOnly", false)
    }
}




