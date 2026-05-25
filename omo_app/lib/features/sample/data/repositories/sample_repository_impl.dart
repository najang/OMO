import '../../domain/entities/sample_entity.dart';
import '../../domain/repositories/sample_repository.dart';
import '../datasources/sample_remote_datasource.dart';
import '../models/sample_model.dart';

class SampleRepositoryImpl implements SampleRepository {
  final SampleRemoteDataSource _remoteDataSource;

  SampleRepositoryImpl(this._remoteDataSource);

  @override
  Future<List<SampleEntity>> getSamples() async {
    final models = await _remoteDataSource.getSamples();
    return models.map((m) => m.toEntity()).toList();
  }

  @override
  Future<SampleEntity> getSampleById(int id) async {
    final model = await _remoteDataSource.getSampleById(id);
    return model.toEntity();
  }
}
