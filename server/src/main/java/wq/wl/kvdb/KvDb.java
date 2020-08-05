package wq.wl.kvdb;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
@Component
public class KvDb {

    @PostConstruct
    public void init() {
        // path = "/tmp/tmp-kv-db";
        RocksDB.loadLibrary();
        Options options = new Options();
        options.setCreateIfMissing(true);
        try {
            rocksDB = RocksDB.open(options, path);
        } catch (RocksDBException e) {
            LOG.error("RocksDB open error: ", e);
            System.exit(-1);
        }
    }

    public String get(String key) throws RocksDBException {
        byte[] bytes = rocksDB.get(key.getBytes());
        if (bytes == null) {
            return null;
        }
        return new String(bytes);
    }

    public void set(String key, String value) throws RocksDBException {
        rocksDB.put(key.getBytes(), value.getBytes());
    }

    public void del(String key) throws RocksDBException {
        rocksDB.delete(key.getBytes());
    }

    private RocksDB rocksDB;

    @Value("${rocksdb.path}")
    private String path;

    private static final Logger LOG = LoggerFactory.getLogger(KvDb.class);

}
