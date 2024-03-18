package artoria.data;

import artoria.common.Loader;

@Deprecated // TODO: can delete
public interface DataLoader extends Loader {

    Object load(Object... args);

}
