# java_metaspace_oom
Minimal app for reproducing OOM Metaspace problem when there are plenty of free metaspace.


## How to compile and run
```
ant run
```

Application output:
```
run:
     [java] Exception catched: javassist.CannotCompileException: by java.lang.OutOfMemoryError: Metaspace
     [java] code_cache_commited = 102400 Kb
     [java] code_cache_commited_peak = 102400 Kb
     [java] code_cache_usage_threshold = 0 Kb
     [java] code_cache_usage_threshold_count = 0 Kb
     [java] code_cache_used = 4449 Kb
     [java] code_cache_used_peak = 4454 Kb
     [java] compressed_class_space_commited = 32768 Kb
     [java] compressed_class_space_commited_peak = 32768 Kb
     [java] compressed_class_space_usage_threshold = 0 Kb
     [java] compressed_class_space_usage_threshold_count = 0 Kb
     [java] compressed_class_space_used = 9062 Kb
     [java] compressed_class_space_used_peak = 9062 Kb
     [java] metaspace_commited = 102400 Kb
     [java] metaspace_commited_peak = 102400 Kb
     [java] metaspace_usage_threshold = 0 Kb
     [java] metaspace_usage_threshold_count = 0 Kb
     [java] metaspace_used = 23564 Kb
     [java] metaspace_used_peak = 23564 Kb
     [java] ps_eden_space_collection_usage_threshold = 0 Kb
     [java] ps_eden_space_collection_usage_threshold_count = 0 Kb
     [java] ps_eden_space_commited = 129024 Kb
     [java] ps_eden_space_commited_peak = 129024 Kb
     [java] ps_eden_space_used = 103218 Kb
     [java] ps_eden_space_used_peak = 129024 Kb
     [java] ps_old_gen_collection_usage_threshold = 0 Kb
     [java] ps_old_gen_collection_usage_threshold_count = 0 Kb
     [java] ps_old_gen_commited = 172032 Kb
     [java] ps_old_gen_commited_peak = 172032 Kb
     [java] ps_old_gen_usage_threshold = 0 Kb
     [java] ps_old_gen_usage_threshold_count = 0 Kb
     [java] ps_old_gen_used = 4184 Kb
     [java] ps_old_gen_used_peak = 4184 Kb
     [java] ps_survivor_space_collection_usage_threshold = 0 Kb
     [java] ps_survivor_space_collection_usage_threshold_count = 0 Kb
     [java] ps_survivor_space_commited = 10752 Kb
     [java] ps_survivor_space_commited_peak = 10752 Kb
     [java] ps_survivor_space_used = 10736 Kb
     [java] ps_survivor_space_used_peak = 10736 Kb
     [java] 
     [java] 
```

My java version is
```
$ java -version
java version "1.8.0_102"
Java(TM) SE Runtime Environment (build 1.8.0_102-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14, mixed mode)
```

## The problem
We have 23564Kb metaspace in use and we've set -XX:MaxMetaspaceSize=100m https://github.com/vlkv/java_metaspace_oom/blob/master/build.xml#L9 at startup. So why JVM throws java.lang.OutOfMemoryError: Metaspace? How to use the whole 100m of reserved memory?

