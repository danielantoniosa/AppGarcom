package com.daniel.appgarcom.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.appgarcom.CadastroActivity;
import com.daniel.appgarcom.R;
import com.daniel.appgarcom.modelo.beans.Empresa;
import com.daniel.appgarcom.modelo.beans.PreferencesSettings;
import com.daniel.appgarcom.modelo.beans.SharedPreferences;
import com.daniel.appgarcom.modelo.beans.Usuario;
import com.daniel.appgarcom.modelo.persistencia.BdEmpresa;
import com.daniel.appgarcom.modelo.persistencia.BdUsuario;
import com.daniel.appgarcom.sync.RestauranteAPI;
import com.daniel.appgarcom.sync.SyncDefaut;
import com.daniel.appgarcom.util.Criptografia;
import com.daniel.appgarcom.util.Data;
import com.daniel.appgarcom.util.MaskEditUtil;
import com.daniel.appgarcom.util.PermissionUtils;
import com.daniel.appgarcom.util.UtilImageTransmit;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {
    private static final String TAG = "PerfilFragment";
    private static final String SELECT_PICTURE_TEXT_NO_PIC = "Selecionar foto";
    private static final String SELECT_PICTURE_TEXT_CHANGE_PIC = "Alterar foto";
    protected static final int RESULT_SPEECH = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE_CODE = 1;
    private AlertDialog alerta;
    private AlertDialog alerta2;
    private EditText nome, email, telefone, rg, cpf, pwd, logradouro, bairro, numero, uf, cidade, complemento, nascimento, cep;
    private Button buttonCadastro;
    private ImageButton map, view_pwd;
    private TextView tvDesc, tvAddImagem;
    private ProgressBar progresAndress;
    //private LinearLayout pic_selection_section;
    ImageView imagemSignin;
    ImageButton addImagemBtn;

    private Usuario u = new Usuario();
    private boolean aux;
    private View view = null;

    private static byte[] fotoUsuario;
    private TextView andres;
    private AutoCompleteTextView pesquisar;
    private ImageButton btnVoz;
    private ImageButton btnPesquisar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        String[] permissoes = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        PermissionUtils.validate(getActivity(), 0, permissoes);
        imagemSignin = (ImageView) view.findViewById(R.id.imagem_signin);

        addImagemBtn = (ImageButton) view.findViewById(R.id.add_image_button_signin);

        tvAddImagem = (TextView) view.findViewById(R.id.add_image_text_signin);

        addImagemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });


        email = (EditText) view.findViewById(R.id.input_email);
        nome = (EditText) view.findViewById(R.id.input_name);
        pwd = (EditText) view.findViewById(R.id.input_senha);
        nascimento = (EditText) view.findViewById(R.id.input_data_nascimento);
        nascimento.addTextChangedListener(MaskEditUtil.mask(nascimento, MaskEditUtil.FORMAT_DATE));
        telefone = (EditText) view.findViewById(R.id.input_telefone);
        telefone.addTextChangedListener(MaskEditUtil.mask(telefone, MaskEditUtil.FORMAT_FONE));
        cpf = (EditText) view.findViewById(R.id.input_cpf);
        cpf.addTextChangedListener(MaskEditUtil.mask(cpf, MaskEditUtil.FORMAT_CPF));
        rg = (EditText) view.findViewById(R.id.input_rg);
        logradouro = (EditText) view.findViewById(R.id.input_logradouro);
        bairro = (EditText) view.findViewById(R.id.input_bairro);
        numero = (EditText) view.findViewById(R.id.input_numero);
        cidade = (EditText) view.findViewById(R.id.input_cidade);
        complemento = (EditText) view.findViewById(R.id.input_complemento);
        uf = (EditText) view.findViewById(R.id.input_uf);
        uf.addTextChangedListener(MaskEditUtil.mask(uf, "##"));
        cep = (EditText) view.findViewById(R.id.input_cep);
        cep.addTextChangedListener(MaskEditUtil.mask(cep, MaskEditUtil.FORMAT_CEP));

        buttonCadastro = (Button) view.findViewById(R.id.buttonSignUp);

        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validaCampos()) {
                    showAletProgress("Aguarde enquanto é cadastrado...");
                    cadastrar();
                } else {
                    Log.d(TAG, "onClick: Campos Vazios");
                    Toast.makeText(getActivity(), "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        BdUsuario bdUsuario = new BdUsuario(getContext());
        setDados(bdUsuario.listar());
        bdUsuario.close();
        return view;
    }

    private void cadastrar() {
        BdUsuario bdUsuario = new BdUsuario(getContext());
        u = bdUsuario.listar();
        bdUsuario.close();
        u.setDataNacimento(Data.formataDataUS(nascimento.getText() + ""));
        u.setNome(nome.getText() + "");
        u.setTelefone(telefone.getText() + "");
        u.setEmail(email.getText() + "");
        u.setSenha(Criptografia.criptografar(pwd.getText() + ""));
        u.setCPF(cpf.getText() + "");
        u.setRG(rg.getText() + "");
        u.setLogradouro(logradouro.getText() + "");
        u.setBairro(bairro.getText() + "");
        u.setCidade(cidade.getText() + "");
        u.setNumero(numero.getText() + "");
        u.setUf(uf.getText() + "");
        u.setCep(cep.getText() + "");
        u.setComplemento(complemento.getText() + "");
        u.setFoto(fotoUsuario);

        Log.i("PASSOU", "Passou 1");

        RestauranteAPI i = SyncDefaut.RETROFIT_RESTAURANTE(getContext()).create(RestauranteAPI.class);
        SharedPreferences s = PreferencesSettings.getAllPreferences(getContext());
        Log.i("IFMG", "Senha: " + u.getSenha());
        final Call<Void> call = i.atualizarFuncionario(new Gson().toJson(u), s.getEmail(), u.getSenha());

        Log.i("USUARIO", "U: " + u.toString());

        //Log.i("PASSOU", "Passou 2: " + fotoUsuario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {

                    String auth = response.headers().get("auth");
                    if (auth.equals("1")) {
                        BdUsuario bdUsuario = new BdUsuario(getContext());
                        bdUsuario.deleteAll();
                        u.setSenha(pwd.getText() + "");
                        bdUsuario.insert(u);
                        bdUsuario.close();
                        Toast.makeText(getContext(), "Usuario Atualizado", Toast.LENGTH_SHORT).show();
                        alerta2.dismiss();

                    } else {
                        Toast.makeText(getContext(), "Algo falhou", Toast.LENGTH_SHORT).show();
                        alerta2.dismiss();
                        Log.i("[IFMG]", "Falhou");
                    }

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Falha ao cadastrar usuario..", Toast.LENGTH_SHORT).show();
                alerta2.dismiss();
                Log.i("[IFMG]", "Falha ao baixar usuários: " + t.getMessage());
                t.printStackTrace();
            }
        });

    }

    private void setDados(Usuario u) {
        nascimento.setText(Data.formataDataBR(u.getDataNacimento()));
        nome.setText(u.getNome());
        telefone.setText(u.getTelefone());
        email.setText(u.getEmail());
        pwd.setText(u.getSenha());
        cpf.setText(u.getCPF());
        rg.setText(u.getRG());
        logradouro.setText(u.getLogradouro());
        bairro.setText(u.getBairro());
        cidade.setText(u.getCidade());
        numero.setText(u.getNumero());
        uf.setText(u.getUf());
        cep.setText(u.getCep());
        complemento.setText(u.getComplemento());
        UtilImageTransmit utilImageTransmit = new UtilImageTransmit();
        Bitmap bitmap = UtilImageTransmit.convertBytetoImage(u.getFoto());
        if (bitmap != null) {
            imagemSignin.setImageBitmap(bitmap);
            imagemSignin.setBackgroundColor(Color.WHITE);
            tvAddImagem.setText("Alterar imagem");
            tvAddImagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakePictureIntent();
                }
            });
            addImagemBtn.setVisibility(View.GONE);


            fotoUsuario = utilImageTransmit.convertImageToByte(bitmap);

            Log.i("FOTO", "" + fotoUsuario);
        } else Log.i("imagem", "imagemNula");
    }

    private boolean validaCampos() {
        if (!nome.getText().toString().equals("") &&
                !pwd.getText().toString().equals("") &&
                !email.getText().toString().equals("") &&
                !nascimento.getText().toString().equals("") &&
                !telefone.getText().toString().equals("") &&
                !cpf.getText().toString().equals("") &&
                !rg.getText().toString().equals("") &&
                !logradouro.getText().toString().equals("") &&
                !bairro.getText().toString().equals("") &&
                !cidade.getText().toString().equals("") &&
                !numero.getText().toString().equals("") &&
                !uf.getText().toString().equals("") &&
                !cep.getText().toString().equals("") &&
                !complemento.getText().toString().equals("") &&
                fotoUsuario != null) {
            return true;
        } else {
            return false;
        }
    }


    public void showAletProgress(final String descricao) {
        final LayoutInflater li = getLayoutInflater();
        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.alert_progress, null);
        tvDesc = (TextView) view.findViewById(R.id.tvDesc);    //definimos para o botão do layout um clickListener
        tvDesc.setText(descricao);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Aguarde...");
        builder.setView(view);
        builder.setCancelable(false);
        alerta2 = builder.create();
        alerta2.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE_CODE && resultCode == getActivity().RESULT_OK) {

            Bundle bundle = data.getExtras();

            //Todo arrumar aq
            Bitmap bitmap = (Bitmap) bundle.get("data");

            if (bitmap != null) {
                imagemSignin.setImageBitmap(bitmap);
                imagemSignin.setBackgroundColor(Color.WHITE);
                tvAddImagem.setText("Alterar imagem");
                tvAddImagem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakePictureIntent();
                    }
                });
                addImagemBtn.setVisibility(View.GONE);

                UtilImageTransmit utilImageTransmit = new UtilImageTransmit();
                fotoUsuario = utilImageTransmit.convertImageToByte(bitmap);

                Log.i("FOTO", "" + fotoUsuario);
            } else Log.i("imagem", "imagemNula");

            //imageView.setImageBitmap(imageBitmap);
        } // TODO:
        /*else if (requestCode == REQUEST_WIFI_CONNECTION && resultCode == getActivity().RESULT_OK) {

            cadastrar2();

        } */
    }


    private void dispatchTakePictureIntent() {
        //galeria
        //Intent.ACTION_GET_CONTENT
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_CODE);
    }
}
