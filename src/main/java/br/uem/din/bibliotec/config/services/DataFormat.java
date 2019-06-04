package br.uem.din.bibliotec.config.services;

import java.time.LocalDate;

public class DataFormat {

    //como o dado é informado como DD/MM/AAAA precisamos convertê-la para o formato do banco de dados
    public String formatadorDatasMySQL(String data) {
        String[] dataSeparada = data.split("/");
        LocalDate data_formatada = LocalDate.of(Integer.parseInt(dataSeparada[2]), Integer.parseInt(dataSeparada[1]), Integer.parseInt(dataSeparada[0]));

        return data_formatada.toString().trim();
    }

    //como o dado é informado como AAAA-MM-DD precisamos convertê-la para o formato do brasileiro ao imprimir no front-end ao usuário
    public String formatadorDatasBrasil(String data) {
        if (data == null) {
            return " ";
        } else {
            String[] dataSeparada = data.split("-");
            String dataPadraoBrasil = dataSeparada[2] + "/" + dataSeparada[1] + "/" + dataSeparada[0];
            return dataPadraoBrasil.trim();
        }
    }
}
