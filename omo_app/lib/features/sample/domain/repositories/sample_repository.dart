import '../entities/sample_entity.dart';

abstract interface class SampleRepository {
  Future<List<SampleEntity>> getSamples();
  Future<SampleEntity> getSampleById(int id);
}
