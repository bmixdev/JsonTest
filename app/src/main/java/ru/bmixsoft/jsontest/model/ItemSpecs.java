package ru.bmixsoft.jsontest.model;

/**
 * Created by Михаил on 17.10.2016.
 */
public class ItemSpecs {
    private String Id;
    private String Collection_Id;
    private String SpecId;
    private String CntTalons;
    public ItemSpecs(){}

    public ItemSpecs(String id, String collection_Id, String specId, String cntTalons) {
        Id = id;
        Collection_Id = collection_Id;
        SpecId = specId;
        CntTalons = cntTalons;
    }

    @Override
    public String toString() {
        return "ItemSpecs{" +
                "Id=" + Id +
                ", Collection_Id=" + Collection_Id +
                ", SpecId=" + SpecId +
                ", CntTalons=" + CntTalons +
                '}';
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCollection_Id() {
        return Collection_Id;
    }

    public void setCollection_Id(String collection_Id) {
        Collection_Id = collection_Id;
    }

    public String getSpecId() {
        return SpecId;
    }

    public void setSpecId(String specId) {
        SpecId = specId;
    }

    public String getCntTalons() {
        return CntTalons;
    }

    public void setCntTalons(String cntTalons) {
        CntTalons = cntTalons;
    }
}
