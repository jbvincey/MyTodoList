package com.jbvincey.mytodolist.features.todolist

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jbvincey.mytodolist.R
import com.jbvincey.mytodolist.features.addtodo.AddTodoActivity
import com.jbvincey.mytodolist.ui.adapter.TodoListAdapter

import kotlinx.android.synthetic.main.activity_todo_list.*
import org.koin.android.viewmodel.ext.android.viewModel

class TodoListActivity : AppCompatActivity() {

    private val viewModel: TodoListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)
        setSupportActionBar(toolbar)

        fabButton.setOnClickListener {
            startActivity(Intent(this, AddTodoActivity::class.java))
        }

        todoRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = TodoListAdapter()
        todoRecyclerView.adapter = adapter
        viewModel.todoList.observe(this, Observer {
            it?.let(adapter::submitList)
        })


    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_todo_list, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}
