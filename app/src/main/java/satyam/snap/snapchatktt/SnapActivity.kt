package satyam.snap.snapchatktt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.snap.snapchatkt.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapActivity : AppCompatActivity() {

    var snapListView : ListView?= null
    var emails : ArrayList<String> = ArrayList()// empty arraylist
    val mAuth = FirebaseAuth.getInstance()
    var snaps : ArrayList<DataSnapshot> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)
        setTitle("Recent Snap ")

        snapListView = this.findViewById(R.id.snapListView)

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, emails)

        snapListView?.adapter = adapter

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid.toString()).child("snaps").addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?)
            {
                snaps.add(p0)
                emails.add(p0?.child("from")?.value as String)
                adapter.notifyDataSetChanged()

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildRemoved(p0: DataSnapshot) {

                for(snap : DataSnapshot in snaps)
                {
                    var index = 0
                    if(snap.key == p0?.key)
                    {
                        snaps.removeAt(index)
                        emails.removeAt(index)
                    }

                    index++
                }

                adapter.notifyDataSetChanged()
            }


        })

        snapListView?.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            Log.i("InsideSnapListView","In")

            val snapshot = snaps.get(i)

            val intent = Intent(this, viewSnapActivity::class.java)


            intent.putExtra("imageName", snapshot.child("imageName").value as String)
            Log.i("imageName", snapshot.child("imageName").value as String)


         var url =  snapshot.child("imageURL").getValue()  // got url here of particular dataSnapshot

            Log.i("imageURL", url.toString())

            intent.putExtra("imageURL", url as String)

            intent.putExtra("message",snapshot.child("message").value as String)
//            Log.i("message",snapshot.child("message").value as String)
            intent.putExtra("snapKey",snapshot?.key)

            startActivity(intent)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.snaps,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.createSnap)
        {
            //  create a snap
            val intent = Intent(this, createSnapActivity::class.java)
            startActivity(intent)

        }
        else if(item?.itemId == R.id.logout)
        {
            mAuth.signOut()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mAuth.signOut()
        super.onBackPressed()



    }
}
