import '../entities/sample_entity.dart';
import '../repositories/sample_repository.dart';

class GetSamplesUseCase {
  final SampleRepository _repository;

  GetSamplesUseCase(this._repository);

  Future<List<SampleEntity>> call() => _repository.getSamples();
}
