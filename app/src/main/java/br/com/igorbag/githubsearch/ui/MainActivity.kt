package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        setupListeners()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
        btnConfirmar = findViewById(R.id.btn_confirmar)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            val textoDigitado = nomeUsuario.text.toString().trim()
            if (textoDigitado.isNotEmpty()) {
                saveUserLocal(textoDigitado)
                Toast.makeText(this@MainActivity, "Nome salvo com sucesso!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal(nome: String) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()){
            putString(getString(R.string.salved_name), nome)
            apply()
        }
    }

    private fun showUserName() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val nomeSalvo = sharedPref.getString(getString(R.string.salved_name), "")
        // Verifica se a preferência contém um valor não vazio
        if (nomeSalvo!!.isNotEmpty()) {
            nomeUsuario.setText(nomeSalvo)
        }
    }


    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        val baseUrl = "https://api.github.com/"
         retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName(user : String) {
        // TODO 6 - realizar a implementacao do callback do retrofit e chamar o metodo setupAdapter se retornar os dados com sucesso

        val gitService = retrofit.create(GitHubService::class.java)
        val call = gitService.getAllRepositoriesByUser(user)

        call.enqueue(object : Callback<List<Repository>> { // Substitua "Repository" pelo tipo de dado real que sua chamada de API retorna
            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                if (response.isSuccessful) {
                    val repositories = response.body()

                    if (repositories != null) {
                        setupAdapter(repositories)
                    } else {
                        Toast.makeText(this@MainActivity, "Erro ao buscar repositórios", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        /*
            @TODO 7 - Implementar a configuracao do Adapter , construir o adapter e instancia-lo
            passando a listagem dos repositorios
         */
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // @Todo 11 - Colocar esse metodo no click do share item do adapter
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio

    // @Todo 12 - Colocar esse metodo no click item do adapter
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}