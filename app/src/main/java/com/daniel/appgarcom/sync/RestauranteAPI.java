package com.daniel.appgarcom.sync;

;

import com.daniel.appgarcom.adapter.holder.Mesa;
import com.daniel.appgarcom.adapter.holder.Pedido;
import com.daniel.appgarcom.adapter.holder.PedidoBEAN;
import com.daniel.appgarcom.modelo.beans.Empresa;
import com.daniel.appgarcom.modelo.beans.Produto;
import com.daniel.appgarcom.modelo.beans.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RestauranteAPI {
    // Servlets para testes no servidor local
    @FormUrlEncoded
    @POST("restaurante_server/LoginEmpresa")
    Call<Empresa> fazLoginEmpresa(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/FazLoginGarcom")
    Call<Usuario> fazLogin(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/ListarPedidosCaixa")
    Call<ArrayList<Pedido>> listarPedidos(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/CancelarPedido")
    Call<Void> cancelarPedido(@Field("pedido") String pedido, @Field("motivo") String motivo, @Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/ListarPedidos")
    Call<ArrayList<Pedido>> listarPedidosPendentes(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/AlterarPedido")
    Call<ArrayList<Pedido>> alterarPedido(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha, @Field("pedido") String pedido);

    @FormUrlEncoded
    @POST("restaurante_server/ListarPedidosRealizados")
    Call<ArrayList<Pedido>> listarPedidosRealizados(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/ListarMesaAberta")
    Call<ArrayList<Mesa>> getMesasAbertas(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/AdicionarFuncionario")
    Call<Void> insereFuncionario(@Field("funcionario") String funcionario, @Field("nomeUsuario") String empresa, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/EditarFuncionario")
    Call<Void> atualizarFuncionario(@Field("funcionario") String funcionario, @Field("nomeUsuario") String empresa, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/ListarProdutos")
    Call<ArrayList<Produto>> listarProdutos(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("restaurante_server/AbrirMesa")
    Call<Void> abrirMesa(@Field("nomeUsuario") String empresa, @Field("senha") String senha, @Field("mesa") String numMesa);

    @FormUrlEncoded
    @POST("restaurante_server/RealizarVenda")
    Call<Void> enviarPedidos(@Field("pedido") String pedido, @Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);


}
