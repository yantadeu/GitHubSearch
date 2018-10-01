package yan.githubsearch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yan on 01/10/2018.
 */
public abstract class Item {

    @SerializedName("number")
    @Expose
    public long number;

    @SerializedName("title")
    @Expose
    public String titulo;

    @SerializedName("body")
    @Expose
    public String descricao;
}
