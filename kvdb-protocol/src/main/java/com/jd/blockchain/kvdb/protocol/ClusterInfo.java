package com.jd.blockchain.kvdb.protocol;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;

@DataContract(code = Constants.CLUSTER_INFO)
public interface ClusterInfo{

    @DataField(order = 0, primitiveType = PrimitiveType.TEXT)
    String getName();

    @DataField(order = 1, list = true, primitiveType = PrimitiveType.TEXT)
    String[] getURLs();

}