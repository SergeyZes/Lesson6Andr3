package lesson5.rumpilstilstkin.ru.databaseex;

import dagger.Component;

@Component(modules = {NetworkInfomodule.class})
public interface AppComponent {
    void injectsToMainActivity(MainActivity mainActivity);
}
