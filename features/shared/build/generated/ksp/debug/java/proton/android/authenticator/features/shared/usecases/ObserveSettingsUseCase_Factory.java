package proton.android.authenticator.features.shared.usecases;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import proton.android.authenticator.shared.common.domain.infrastructure.queries.QueryBus;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ObserveSettingsUseCase_Factory implements Factory<ObserveSettingsUseCase> {
  private final Provider<QueryBus> queryBusProvider;

  public ObserveSettingsUseCase_Factory(Provider<QueryBus> queryBusProvider) {
    this.queryBusProvider = queryBusProvider;
  }

  @Override
  public ObserveSettingsUseCase get() {
    return newInstance(queryBusProvider.get());
  }

  public static ObserveSettingsUseCase_Factory create(
      javax.inject.Provider<QueryBus> queryBusProvider) {
    return new ObserveSettingsUseCase_Factory(Providers.asDaggerProvider(queryBusProvider));
  }

  public static ObserveSettingsUseCase_Factory create(Provider<QueryBus> queryBusProvider) {
    return new ObserveSettingsUseCase_Factory(queryBusProvider);
  }

  public static ObserveSettingsUseCase newInstance(QueryBus queryBus) {
    return new ObserveSettingsUseCase(queryBus);
  }
}
