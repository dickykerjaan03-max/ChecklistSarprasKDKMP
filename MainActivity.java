package id.datapegangan.sarpraskdkmp;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import android.text.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    int navy=Color.rgb(24,55,100), blue=Color.rgb(25,105,235), pale=Color.rgb(239,245,255);
    int ink=Color.rgb(20,46,91), muted=Color.rgb(100,121,154), line=Color.rgb(222,233,249);
    DB db; LinearLayout root, body, itemList; EditText search; String q="";
    String filter="Semua Status"; int photoTarget=-1; static final int PICK_PHOTO=77;

    @Override public void onCreate(Bundle b){super.onCreate(b); db=new DB(this); seed(); render();}

    void seed(){
        if(db.count()>0)return;
        db.add("Meja kasir","Peralatan Kantor","Area pelayanan",1,"-","");
        db.add("APAR","Keselamatan","Area pelayanan",1,"Belum diverifikasi","");
        db.add("Truck","Kendaraan","Halaman/garasi",1,"-","");
    }

    void render(){
        root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(Color.rgb(246,249,255));setContentView(root);
        LinearLayout top=new LinearLayout(this);top.setOrientation(1);top.setPadding(dp(20),dp(20),dp(20),dp(18));top.setBackgroundColor(navy);
        TextView brand=txt("KOPDES MERAH PUTIH  •  DATA PEGANGAN",12,Color.rgb(193,216,255),true);
        TextView title=txt("Checklist Sarpras",26,Color.WHITE,true);
        TextView sub=txt("KDKMP DESA BUGEL POLOKARTO",14,Color.rgb(225,235,255),true);
        TextView manager=txt("Manager: DICKY NUR HIDAYAT, S.Kom",12,Color.WHITE,false);
        top.addView(brand);top.addView(gap(8));top.addView(title);top.addView(gap(5));top.addView(sub);top.addView(gap(10));top.addView(manager);root.addView(top);
        ScrollView sc=new ScrollView(this);body=new LinearLayout(this);body.setOrientation(1);body.setPadding(dp(16),dp(16),dp(16),dp(28));sc.addView(body);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        welcomeBanner();
        summary();
        categorySection();
        TextView section=txt("Daftar Barang & Alat",20,ink,true);section.setPadding(0,dp(20),0,dp(10));body.addView(section);
        LinearLayout searchRow=new LinearLayout(this);searchRow.setOrientation(0);
        search=new EditText(this);search.setSingleLine(true);search.setTextSize(14);search.setHint("Cari barang atau alat...");search.setPadding(dp(12),0,dp(12),0);search.setBackground(shape(Color.WHITE,dp(12),line));
        searchRow.addView(search,new LinearLayout.LayoutParams(-1,dp(48)));body.addView(searchRow);
        search.setText(q);search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){q=s.toString();refresh();}public void afterTextChanged(Editable e){}});
        Spinner sp=new Spinner(this);String[] opts={"Semua Status","Terpenuhi","Dalam Proses","Belum Terpenuhi"};sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,opts));body.addView(sp);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int pos,long id){filter=opts[pos];refresh();}});
        Button add=btn("+  Tambah Barang",blue,Color.WHITE);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,dp(48));bp.topMargin=dp(8);body.addView(add,bp);add.setOnClickListener(v->form(null));
        itemList=new LinearLayout(this);itemList.setOrientation(1);body.addView(itemList);refresh();
        TextView foot=txt("Data tersimpan offline di perangkat ini.",11,muted,false);foot.setGravity(Gravity.CENTER);foot.setPadding(0,dp(18),0,0);body.addView(foot);
        root.addView(bottomNavigation());
    }

    void welcomeBanner(){
        LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(16),dp(16),dp(16),dp(16));
        card.setBackground(shape(Color.rgb(229,240,255),dp(16),Color.rgb(211,229,255)));
        card.addView(txt("Selamat Datang,",14,blue,true));
        card.addView(gap(4));
        card.addView(txt("DICKY NUR HIDAYAT, S.Kom",17,ink,true));
        card.addView(gap(5));
        card.addView(txt("Kelola dan pantau kebutuhan sarana prasarana KDKMP dengan mudah dan cepat.",12,muted,false));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(14);body.addView(card,p);
    }

    void categorySection(){
        TextView heading=txt("Kategori Barang",17,ink,true);heading.setPadding(0,dp(18),0,dp(9));body.addView(heading);
        LinearLayout row=new LinearLayout(this);row.setOrientation(1);
        String[] cats={"Perabotan","Keselamatan","Kendaraan","Infrastruktur"};
        String[] icons={"▣","!","▰","▥"};
        int[] colors={Color.rgb(130,91,220),Color.rgb(225,67,76),Color.rgb(10,174,164),blue};
        for(int i=0;i<cats.length;i++){
            LinearLayout c=new LinearLayout(this);c.setOrientation(0);c.setGravity(Gravity.CENTER_VERTICAL);c.setPadding(dp(12),dp(10),dp(12),dp(10));c.setBackground(shape(Color.WHITE,dp(12),line));
            LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.bottomMargin=dp(7);row.addView(c,cp);
            TextView icon=txt(icons[i],20,Color.WHITE,true);icon.setGravity(Gravity.CENTER);icon.setBackground(shape(colors[i],dp(24),colors[i]));c.addView(icon,new LinearLayout.LayoutParams(dp(42),dp(42)));
            LinearLayout labels=new LinearLayout(this);labels.setOrientation(1);labels.setPadding(dp(12),0,0,0);
            labels.addView(txt(cats[i],14,ink,true));labels.addView(txt(db.countCategory(cats[i])+" item",11,muted,false));
            c.addView(labels,new LinearLayout.LayoutParams(0,-2,1));c.addView(txt("›",24,blue,true));
        }
        body.addView(row);
    }

    View bottomNavigation(){
        LinearLayout nav=new LinearLayout(this);nav.setOrientation(0);nav.setPadding(dp(5),dp(8),dp(5),dp(8));nav.setBackgroundColor(Color.WHITE);
        String[] labels={"Beranda","Checklist","Foto","Laporan"};String[] symbols={"⌂","☷","▧","▥"};
        for(int i=0;i<labels.length;i++){
            LinearLayout cell=new LinearLayout(this);cell.setOrientation(1);cell.setGravity(Gravity.CENTER);
            TextView ico=txt(symbols[i],22,i==0?blue:muted,true);TextView lab=txt(labels[i],10,i==0?blue:muted,i==0);cell.addView(ico);cell.addView(lab);
            nav.addView(cell,new LinearLayout.LayoutParams(0,dp(48),1));
        }
        return nav;
    }

    void summary(){
        TextView h=txt("Ringkasan Kebutuhan",17,ink,true);body.addView(h);body.addView(gap(10));
        int total=db.count(), photos=db.photos(), missing=db.countStatus("Belum Terpenuhi")+db.countStatus("Belum diverifikasi");
        LinearLayout row=new LinearLayout(this);row.setOrientation(0);
        row.addView(stat("Total Barang",total,blue),new LinearLayout.LayoutParams(0,dp(92),1));row.addView(gapW(8),new LinearLayout.LayoutParams(dp(8),1));
        row.addView(stat("Foto Tersedia",photos,Color.rgb(20,169,112)),new LinearLayout.LayoutParams(0,dp(92),1));body.addView(row);body.addView(gap(8));
        LinearLayout row2=new LinearLayout(this);row2.setOrientation(0);
        row2.addView(stat("Perlu Dilengkapi",missing,Color.rgb(225,67,76)),new LinearLayout.LayoutParams(0,dp(92),1));row2.addView(gapW(8),new LinearLayout.LayoutParams(dp(8),1));
        row2.addView(stat("Kategori",db.categories(),Color.rgb(130,91,220)),new LinearLayout.LayoutParams(0,dp(92),1));body.addView(row2);
    }
    View stat(String label,int value,int color){LinearLayout c=new LinearLayout(this);c.setOrientation(1);c.setPadding(dp(12),dp(12),dp(8),dp(8));c.setBackground(shape(Color.WHITE,dp(14),line));c.addView(txt(label,11,muted,true));TextView n=txt(""+value,26,color,true);c.addView(gap(4));c.addView(n);return c;}

    void refresh(){
        if(itemList==null)return;itemList.removeAllViews();
        ArrayList<Item> data=db.list(q,filter);
        if(data.size()==0){TextView e=txt("Data tidak ditemukan. Tekan Tambah Barang untuk mengisi checklist.",14,muted,false);e.setPadding(0,dp(25),0,dp(25));itemList.addView(e);return;}
        for(Item it:data){
            LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(13),dp(12),dp(13),dp(12));card.setBackground(shape(Color.WHITE,dp(14),line));
            LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.topMargin=dp(9);itemList.addView(card,cp);
            LinearLayout first=new LinearLayout(this);first.setGravity(Gravity.CENTER_VERTICAL);first.setOrientation(0);
            TextView no=txt(String.format(Locale.getDefault(),"%02d",it.id),13,blue,true);first.addView(no);first.addView(gapW(10));
            LinearLayout nameCol=new LinearLayout(this);nameCol.setOrientation(1);
            nameCol.addView(txt(it.name,16,ink,true));nameCol.addView(txt(it.category+" • "+it.location,11,muted,false));
            first.addView(nameCol,new LinearLayout.LayoutParams(0,-2,1));
            if(!it.photo.isEmpty()){
                ImageView image=new ImageView(this);image.setImageURI(Uri.parse(it.photo));image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setBackground(shape(pale,dp(9),line));first.addView(image,new LinearLayout.LayoutParams(dp(58),dp(58)));
            } else {
                TextView noPhoto=txt("FOTO\n—",10,muted,true);noPhoto.setGravity(Gravity.CENTER);noPhoto.setBackground(shape(pale,dp(9),line));first.addView(noPhoto,new LinearLayout.LayoutParams(dp(58),dp(58)));
            }
            card.addView(first);card.addView(gap(8));
            LinearLayout detail=new LinearLayout(this);detail.setOrientation(0);detail.setGravity(Gravity.CENTER_VERTICAL);
            detail.addView(txt("Jumlah: "+it.qty+" unit",12,ink,true),new LinearLayout.LayoutParams(0,-2,1));
            TextView status=txt(it.status,11,statusColor(it.status),true);status.setPadding(dp(8),dp(5),dp(8),dp(5));status.setBackground(shape(statusBg(it.status),dp(20),statusBg(it.status)));detail.addView(status);card.addView(detail);
            if(!it.note.equals("-")&&!it.note.isEmpty()){TextView note=txt("Keterangan: "+it.note,12,muted,false);note.setPadding(0,dp(7),0,0);card.addView(note);}
            LinearLayout actions=new LinearLayout(this);actions.setOrientation(0);actions.setPadding(0,dp(8),0,0);
            Button photo=mini("Tambah Foto"),edit=mini("Edit"),del=mini("Hapus");
            actions.addView(photo,new LinearLayout.LayoutParams(0,dp(38),1));actions.addView(edit,new LinearLayout.LayoutParams(0,dp(38),1));actions.addView(del,new LinearLayout.LayoutParams(0,dp(38),1));card.addView(actions);
            photo.setOnClickListener(v->{photoTarget=it.id;Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);intent.addCategory(Intent.CATEGORY_OPENABLE);intent.setType("image/*");startActivityForResult(intent,PICK_PHOTO);});
            edit.setOnClickListener(v->form(it));
            del.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Hapus barang?").setMessage(it.name).setNegativeButton("Batal",null).setPositiveButton("Hapus",(d,w)->{db.remove(it.id);render();}).show());
        }
    }

    void form(Item item){
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(dp(18),dp(4),dp(18),0);
        EditText name=field("Nama barang/alat",item==null?"":item.name),qty=field("Jumlah",item==null?"1":""+item.qty),loc=field("Lokasi",item==null?"":item.location),note=field("Keterangan",item==null?"-":item.note);
        qty.setInputType(2);
        Spinner cat=spin(new String[]{"Peralatan Kantor","Elektronik","Perabotan","Infrastruktur","Keselamatan","Kendaraan"},item==null?"Peralatan Kantor":item.category);
        Spinner st=spin(new String[]{"Belum Terpenuhi","Dalam Proses","Terpenuhi","Belum diverifikasi"},item==null?"Belum diverifikasi":item.status);
        box.addView(name);box.addView(gap(5));box.addView(qty);box.addView(gap(5));box.addView(loc);box.addView(gap(5));box.addView(note);box.addView(gap(5));box.addView(cat);box.addView(gap(5));box.addView(st);
        new AlertDialog.Builder(this).setTitle(item==null?"Tambah Barang":"Edit Barang").setView(box).setNegativeButton("Batal",null).setPositiveButton("Simpan",(d,w)->{
            String n=name.getText().toString().trim(),l=loc.getText().toString().trim();int amount;
            try{amount=Integer.parseInt(qty.getText().toString());}catch(Exception e){amount=0;}
            if(n.isEmpty()||l.isEmpty()||amount<1){Toast.makeText(this,"Nama, lokasi, dan jumlah wajib diisi dengan benar.",Toast.LENGTH_LONG).show();return;}
            if(item==null)db.add(n,cat.getSelectedItem().toString(),l,amount,st.getSelectedItem().toString(),note.getText().toString().trim());
            else db.update(item.id,n,cat.getSelectedItem().toString(),l,amount,st.getSelectedItem().toString(),note.getText().toString().trim(),item.photo);
            render();
        }).show();
    }

    @Override protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(req==PICK_PHOTO&&res==RESULT_OK&&data!=null&&data.getData()!=null){Uri u=data.getData();try{getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}db.photo(photoTarget,u.toString());render();}}
    EditText field(String hint,String value){EditText e=new EditText(this);e.setSingleLine(true);e.setTextSize(14);e.setHint(hint);e.setText(value);return e;}
    Spinner spin(String[] values,String selected){Spinner s=new Spinner(this);s.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,values));for(int i=0;i<values.length;i++)if(values[i].equals(selected))s.setSelection(i);return s;}
    Button btn(String s,int bg,int fg){Button b=new Button(this);b.setText(s);b.setTextColor(fg);b.setTextSize(14);b.setAllCaps(false);b.setBackground(shape(bg,dp(12),bg));return b;}
    Button mini(String s){Button b=new Button(this);b.setText(s);b.setTextSize(10);b.setAllCaps(false);b.setTextColor(blue);b.setBackground(shape(pale,dp(9),line));return b;}
    TextView txt(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,1);return t;}
    View gap(int h){View v=new View(this);v.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h)));return v;}
    View gapW(int w){View v=new View(this);v.setLayoutParams(new LinearLayout.LayoutParams(dp(w),1));return v;}
    int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
    GradientDrawable shape(int c,int r,int stroke){GradientDrawable d=new GradientDrawable();d.setColor(c);d.setCornerRadius(r);d.setStroke(dp(1),stroke);return d;}
    int statusColor(String s){if(s.equals("Terpenuhi"))return Color.rgb(15,145,94);if(s.equals("Dalam Proses"))return Color.rgb(181,116,7);return Color.rgb(204,54,63);}
    int statusBg(String s){if(s.equals("Terpenuhi"))return Color.rgb(221,248,235);if(s.equals("Dalam Proses"))return Color.rgb(255,243,211);return Color.rgb(255,229,231);}

    static class Item {int id,qty;String name,category,location,status,note,photo;Item(int i,String n,String c,String l,int q,String s,String no,String p){id=i;name=n;category=c;location=l;qty=q;status=s;note=no;photo=p;}}
    static class DB extends SQLiteOpenHelper {
        DB(Context c){super(c,"sarpras_kdkmp.db",null,1);}
        public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE items(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,category TEXT,location TEXT,qty INTEGER,status TEXT,note TEXT,photo TEXT)");}
        public void onUpgrade(SQLiteDatabase d,int o,int n){}
        int count(){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM items",null);c.moveToFirst();int x=c.getInt(0);c.close();return x;}
        int photos(){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM items WHERE photo IS NOT NULL AND photo!=''",null);c.moveToFirst();int x=c.getInt(0);c.close();return x;}
        int categories(){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(DISTINCT category) FROM items",null);c.moveToFirst();int x=c.getInt(0);c.close();return x;}
        int countCategory(String category){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM items WHERE category=?",new String[]{category});c.moveToFirst();int x=c.getInt(0);c.close();return x;}
        int countStatus(String s){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM items WHERE status=?",new String[]{s});c.moveToFirst();int x=c.getInt(0);c.close();return x;}
        void add(String n,String c,String l,int q,String s,String no){ContentValues v=new ContentValues();v.put("name",n);v.put("category",c);v.put("location",l);v.put("qty",q);v.put("status",s);v.put("note",no);v.put("photo","");getWritableDatabase().insert("items",null,v);}
        void update(int id,String n,String c,String l,int q,String s,String no,String p){ContentValues v=new ContentValues();v.put("name",n);v.put("category",c);v.put("location",l);v.put("qty",q);v.put("status",s);v.put("note",no);v.put("photo",p);getWritableDatabase().update("items",v,"id=?",new String[]{""+id});}
        void photo(int id,String p){ContentValues v=new ContentValues();v.put("photo",p);getWritableDatabase().update("items",v,"id=?",new String[]{""+id});}
        void remove(int id){getWritableDatabase().delete("items","id=?",new String[]{""+id});}
        ArrayList<Item> list(String q,String st){ArrayList<Item>a=new ArrayList<>();String sql="SELECT id,name,category,location,qty,status,note,photo FROM items WHERE (name LIKE ? OR category LIKE ? OR location LIKE ?)";ArrayList<String> args=new ArrayList<>();String term="%"+q+"%";args.add(term);args.add(term);args.add(term);if(!st.equals("Semua Status")){sql+=" AND status=?";args.add(st);}sql+=" ORDER BY id";Cursor c=getReadableDatabase().rawQuery(sql,args.toArray(new String[0]));while(c.moveToNext())a.add(new Item(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4),c.getString(5),c.getString(6),c.getString(7)));c.close();return a;}
    }
}
