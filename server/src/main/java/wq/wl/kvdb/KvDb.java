package wq.wl.kvdb;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
@Component
public class KvDb {
    private RocksDB rocksDB;

    public KvDb() {
        this("");
    }

    public KvDb(String path) {
        path = "/tmp/tmp-kv-db";
        RocksDB.loadLibrary();
        Options options = new Options();
        options.setCreateIfMissing(true);
        try {
            rocksDB = RocksDB.open(options, path);
        } catch (RocksDBException e) {
            LOG.error("RocksDB open error: ", e);
            // todo
            System.exit(-1);
        }
    }

    public String get(String key) {
        try {
            byte[] bytes = rocksDB.get(key.getBytes());
            if (bytes == null) {
                return "null";
            }
            return new String(bytes);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(String key, String value) {
        try {
            rocksDB.put(key.getBytes(), value.getBytes());
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public void del(String key) {
        try {
            rocksDB.delete(key.getBytes());
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(KvDb.class);

}
